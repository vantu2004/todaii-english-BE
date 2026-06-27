package com.todaii.english.client.learning.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.client.learning.repository.AiStudyPlanRepository;
import com.todaii.english.client.learning.repository.DailyStudyLogRepository;
import com.todaii.english.client.learning.repository.NotificationRepository;
import com.todaii.english.client.learning.repository.UserLearningProfileRepository;
import com.todaii.english.core.entity.learning.AiStudyPlan;
import com.todaii.english.core.entity.learning.DailyStudyLog;
import com.todaii.english.core.entity.learning.Notification;
import com.todaii.english.core.entity.learning.UserLearningProfile;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.core.service.SmtpService;
import com.todaii.english.infra.service.AiFallbackService;
import com.todaii.english.shared.dto.learning.PartAccuracyDTO;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.NotificationType;
import com.todaii.english.shared.enums.UserStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiStudyPlanScheduler {
  private final UserLearningProfileRepository userLearningProfileRepository;
  private final DailyStudyLogRepository dailyStudyLogRepository;
  private final AiStudyPlanRepository aiStudyPlanRepository;
  private final NotificationRepository notificationRepository;
  private final AnalyticsService analyticsService;
  private final AiFallbackService aiFallbackService;
  private final SmtpService smtpService;
  private final ObjectMapper objectMapper;

  @Value("classpath:/promptTemplates/systemPromptAiCoachTemplate.st")
  private Resource systemPromptAiCoachTemplate;

  /** Chạy lúc 07:00 sáng, mỗi 2 ngày 1 lần. */
  @Scheduled(cron = "0 0 7 */2 * *")
  @Transactional
  public void generateAiStudyPlans() {
    log.info("⏰ Starting AI Study Plan generation job...");

    List<UserLearningProfile> activeProfiles =
        userLearningProfileRepository.findByTargetScoreGreaterThan(0);

    log.info("Found {} active profiles for AI study plan generation.", activeProfiles.size());

    for (UserLearningProfile profile : activeProfiles) {
      User user = profile.getUser();
      if (!(user != null
          && !Boolean.TRUE.equals(user.getIsDeleted())
          && Boolean.TRUE.equals(user.getEnabled())
          && user.getStatus() == UserStatus.ACTIVE)) {
        continue;
      }

      try {
        log.info("Generating AI study plan for user: {} ({})", user.getDisplayName(), user.getId());

        // a. Lấy dữ liệu Weakness (gọi AnalyticsService.getWeaknessAnalysis()) -> top 2 Part yếu
        // nhất -> JSON string.
        List<PartAccuracyDTO> weakness = analyticsService.getWeaknessAnalysis(user.getId());
        List<PartAccuracyDTO> weakParts =
            weakness.stream()
                .filter(p -> p.getTotal() > 0)
                .sorted((a, b) -> Double.compare(a.getAccuracy(), b.getAccuracy()))
                .limit(2)
                .collect(Collectors.toList());
        String weakPartsJson = objectMapper.writeValueAsString(weakParts);

        // b. Lấy currentStreak từ User.
        int streak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;

        // c. Lấy tổng thời gian học 7 ngày gần nhất từ DailyStudyLog -> chia 7 = avgMinutes.
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        List<DailyStudyLog> logs =
            dailyStudyLogRepository.findByUserIdAndDateBetween(user.getId(), startDate, today);
        int totalMinutes = logs.stream().mapToInt(DailyStudyLog::getTotalStudyMinutes).sum();
        double avgMinutes = Math.round((totalMinutes / 7.0) * 10.0) / 10.0;

        int currentScore = profile.getCurrentScore() != null ? profile.getCurrentScore() : 0;
        int targetScore = profile.getTargetScore();
        String examDateStr =
            profile.getExamDate() != null ? profile.getExamDate().toString() : "Chưa đăng ký";

        // d. Gọi AiFallbackService.executeWithFallback() truyền system prompt + data.
        ChatResponse response =
            aiFallbackService.executeWithFallback(
                user.getId(),
                ActorType.USER,
                client ->
                    client
                        .prompt()
                        .system(
                            promptSystemSpec ->
                                promptSystemSpec
                                    .text(systemPromptAiCoachTemplate)
                                    .param("target_score", String.valueOf(targetScore))
                                    .param("current_score", String.valueOf(currentScore))
                                    .param("exam_date", examDateStr)
                                    .param("streak", String.valueOf(streak))
                                    .param("avg_minutes", String.valueOf(avgMinutes))
                                    .param("weak_parts_json", weakPartsJson))
                        .user("Hãy tạo kế hoạch học tập chi tiết cho tôi dựa trên thông tin trên.")
                        .call()
                        .chatResponse());

        String planContent = response.getResult().getOutput().getText();

        // 3. Kết quả AI trả về (Markdown string):
        // a. Lưu vào bảng ai_study_plans để user xem lại.
        AiStudyPlan studyPlan = AiStudyPlan.builder().content(planContent).user(user).build();
        aiStudyPlanRepository.save(studyPlan);

        // b. Tạo Notification in-app (type = PLAN_READY)
        String previewText =
            planContent.length() > 100 ? planContent.substring(0, 97) + "..." : planContent;
        Notification notification =
            Notification.builder()
                .title("📋 Kế hoạch học mới!")
                .content("Kế hoạch học tập 2 ngày tới thiết kế bởi AI đã sẵn sàng: " + previewText)
                .type(NotificationType.PLAN_READY)
                .user(user)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // c. Gửi email qua smtpService.sendStudyPlanEmail(email, name, planContent) với @Async.
        smtpService.sendStudyPlanEmail(user.getEmail(), user.getDisplayName(), planContent);

      } catch (Exception e) {
        log.error(
            "Warning: Failed to generate AI study plan for user: {} (ID: {}). Skipping to next"
                + " user.",
            user.getDisplayName(),
            user.getId(),
            e);
      }
    }

    log.info("AI Study Plan generation job finished.");
  }
}
