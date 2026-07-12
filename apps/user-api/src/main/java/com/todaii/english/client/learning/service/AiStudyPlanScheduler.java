package com.todaii.english.client.learning.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.client.article.ArticleRepository;
import com.todaii.english.client.learning.repository.AiStudyPlanRepository;
import com.todaii.english.client.learning.repository.DailyStudyLogRepository;
import com.todaii.english.client.learning.repository.StudyPlanTaskRepository;
import com.todaii.english.client.learning.repository.UserLearningProfileRepository;
import com.todaii.english.client.toeic_test.TestRepository;
import com.todaii.english.client.video.VideoRepository;
import com.todaii.english.client.vocabulary.VocabDeckRepository;
import com.todaii.english.core.entity.learning.AiStudyPlan;
import com.todaii.english.core.entity.learning.DailyStudyLog;
import com.todaii.english.core.entity.learning.StudyPlanTask;
import com.todaii.english.core.entity.learning.UserLearningProfile;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.infra.service.AiFallbackService;
import com.todaii.english.shared.dto.learning.PartAccuracyDTO;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.enums.StudyPlanTaskType;
import com.todaii.english.shared.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiStudyPlanScheduler {
  private final UserLearningProfileRepository userLearningProfileRepository;
  private final DailyStudyLogRepository dailyStudyLogRepository;
  private final AiStudyPlanRepository aiStudyPlanRepository;
  private final StudyPlanTaskRepository studyPlanTaskRepository;
  private final ArticleRepository articleRepository;
  private final VideoRepository videoRepository;
  private final VocabDeckRepository vocabDeckRepository;
  private final TestRepository testRepository;
  private final AnalyticsService analyticsService;
  private final AiFallbackService aiFallbackService;
  private final ObjectMapper objectMapper;

  @Value("classpath:/promptTemplates/learning/systemPromptAiCoachTemplate.st")
  private Resource systemPromptAiCoachTemplate;

  @Value("classpath:/promptTemplates/learning/userPromptAiCoachTemplate.st")
  private Resource userPromptAiCoachTemplate;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AiTaskJson {
    private String plan_date;
    private String task_type;
    private Long content_id;
    private String title;
    private String description;
    private Integer estimated_minutes;
    private Boolean in_progress;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AiPlanJson {
    private List<AiTaskJson> tasks;
  }

  /** Chạy lúc 07:00 sáng, mỗi 2 ngày 1 lần. */
  @Scheduled(cron = "0 0 7 */2 * *")
  //   @Scheduled(cron = "0 * * * * *")
  @Transactional
  public void generateAiStudyPlans() {
    log.info("⏰ Starting AI Study Plan generation job...");

    List<UserLearningProfile> activeProfiles =
        userLearningProfileRepository.findByTargetScoreGreaterThan(0);

    log.info("Found {} active profiles for AI study plan generation.", activeProfiles.size());

    for (UserLearningProfile profile : activeProfiles) {
      User user = profile.getUser();
      if (!isActiveUser(user)) {
        continue;
      }

      try {
        log.info("Generating AI study plan for user: {} ({})", user.getDisplayName(), user.getId());
        generatePlanForUser(profile);
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

  private boolean isActiveUser(User user) {
    return user != null
        && !Boolean.TRUE.equals(user.getIsDeleted())
        && Boolean.TRUE.equals(user.getEnabled())
        && user.getStatus() == UserStatus.ACTIVE;
  }

  private void generatePlanForUser(UserLearningProfile profile) throws Exception {
    User user = profile.getUser();
    Long userId = user.getId();

    // 1. Thu thập thông tin học tập của user
    // a. Lấy dữ liệu Weakness
    List<PartAccuracyDTO> weakness = analyticsService.getWeaknessAnalysis(userId);
    List<PartAccuracyDTO> weakParts =
        weakness.stream()
            .filter(p -> p.getTotal() > 0)
            .sorted((a, b) -> Double.compare(a.getAccuracy(), b.getAccuracy()))
            .limit(2)
            .collect(Collectors.toList());
    String weakPartsJson = objectMapper.writeValueAsString(weakParts);

    // Tìm weakest part
    Integer weakestPart = weakParts.isEmpty() ? null : weakParts.get(0).getPart();

    // b. Lấy currentStreak
    int streak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;

    // c. Lấy avgMinutes
    LocalDate today = LocalDate.now();
    LocalDate startDate = today.minusDays(6);
    List<DailyStudyLog> logs =
        dailyStudyLogRepository.findByUserIdAndDateBetween(userId, startDate, today);
    int totalMinutes = logs.stream().mapToInt(DailyStudyLog::getTotalStudyMinutes).sum();
    double avgMinutes = Math.round((totalMinutes / 7.0) * 10.0) / 10.0;

    int currentScore = profile.getCurrentScore() != null ? profile.getCurrentScore() : 0;
    int targetScore = profile.getTargetScore();
    String examDateStr =
        profile.getExamDate() != null ? profile.getExamDate().toString() : "Chưa đăng ký";

    CefrLevel cefrLevel = RecommendationService.getCefrLevel(currentScore);

    // Xác định 2 ngày của plan
    LocalDate planDate1 = LocalDate.now();
    LocalDate planDate2 = planDate1.plusDays(1);

    // 2. Khởi tạo tools và gọi AI
    StudyPlanTools tools =
        new StudyPlanTools(
            userId,
            cefrLevel.name(),
            weakestPart,
            articleRepository,
            videoRepository,
            vocabDeckRepository,
            testRepository);

    ChatResponse response =
        aiFallbackService.executeWithFallback(
            userId,
            ActorType.USER,
            client ->
                client
                    .prompt()
                    .system(promptSystemSpec -> promptSystemSpec.text(systemPromptAiCoachTemplate))
                    .user(
                        promptUserSpec ->
                            promptUserSpec
                                .text(userPromptAiCoachTemplate)
                                .param("target_score", String.valueOf(targetScore))
                                .param("current_score", String.valueOf(currentScore))
                                .param("exam_date", examDateStr)
                                .param("streak", String.valueOf(streak))
                                .param("avg_minutes", String.valueOf(avgMinutes))
                                .param("weak_parts_json", weakPartsJson)
                                .param("plan_date_1", planDate1.toString())
                                .param("plan_date_2", planDate2.toString()))
                    .tools(tools)
                    .call()
                    .chatResponse());

    String rawContent = response.getResult().getOutput().getText();
    log.info("AI raw response: {}", rawContent);

    String cleanJson = cleanAiResponse(rawContent);
    AiPlanJson planJson = objectMapper.readValue(cleanJson, AiPlanJson.class);

    // 3. Save AiStudyPlan container
    AiStudyPlan studyPlan = AiStudyPlan.builder().user(user).build();
    studyPlan = aiStudyPlanRepository.save(studyPlan);

    // 4. Map & Save tasks
    List<StudyPlanTask> tasks = new ArrayList<>();
    if (planJson.getTasks() != null) {
      for (AiTaskJson tJson : planJson.getTasks()) {
        LocalDate pDate;
        try {
          pDate = LocalDate.parse(tJson.getPlan_date());
        } catch (Exception e) {
          pDate = tJson.getPlan_date().contains("2") ? planDate2 : planDate1;
        }

        StudyPlanTaskType type;
        try {
          type = StudyPlanTaskType.valueOf(tJson.getTask_type());
        } catch (Exception e) {
          continue; // Bỏ qua task nếu không đúng type
        }

        StudyPlanTask task =
            StudyPlanTask.builder()
                .studyPlan(studyPlan)
                .planDate(pDate)
                .taskType(type)
                .contentId(tJson.getContent_id())
                .title(tJson.getTitle())
                .description(tJson.getDescription())
                .estimatedMinutes(
                    tJson.getEstimated_minutes() != null ? tJson.getEstimated_minutes() : 15)
                .inProgress(Boolean.TRUE.equals(tJson.getIn_progress()))
                .completed(false)
                .build();
        tasks.add(task);
      }
    }
    studyPlanTaskRepository.saveAll(tasks);
    studyPlan.setTasks(tasks);
  }

  private String cleanAiResponse(String rawText) {
    if (rawText == null) return "{}";
    rawText = rawText.trim();
    if (rawText.startsWith("```json")) {
      rawText = rawText.substring(7);
    } else if (rawText.startsWith("```")) {
      rawText = rawText.substring(3);
    }
    if (rawText.endsWith("```")) {
      rawText = rawText.substring(0, rawText.length() - 3);
    }
    return rawText.trim();
  }
}
