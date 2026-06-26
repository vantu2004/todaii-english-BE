package com.todaii.english.client.learning.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.learning.repository.NotificationRepository;
import com.todaii.english.client.learning.repository.UserLearningProfileRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.learning.Notification;
import com.todaii.english.core.entity.learning.UserLearningProfile;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.core.service.SmtpService;
import com.todaii.english.shared.enums.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyReminderScheduler {
  private final UserRepository userRepository;
  private final UserLearningProfileRepository userLearningProfileRepository;
  private final NotificationRepository notificationRepository;
  private final SmtpService smtpService;

  /** Cảnh báo Mất Streak (Chạy lúc 19:00 hàng ngày) */
  @Scheduled(cron = "0 0 19 * * *")
  @Transactional
  public void checkStreakRisk() {
    log.info("⏰ Starting Streak Risk check job...");
    LocalDate today = LocalDate.now();
    List<User> usersAtRisk =
        userRepository.findByCurrentStreakGreaterThanAndLastStudyDateBefore(0, today);

    log.info("Found {} users at risk of losing streak.", usersAtRisk.size());
    for (User user : usersAtRisk) {
      try {
        // Tạo Notification in-app
        Notification notification =
            Notification.builder()
                .title("🔥 Cảnh báo mất Streak!")
                .content(
                    "Học ngay 5 phút để bảo vệ chuỗi "
                        + user.getCurrentStreak()
                        + " ngày học liên tiếp nào!")
                .type(NotificationType.STREAK_RISK)
                .user(user)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Gửi email qua smtpService
        smtpService.sendStreakRiskEmail(
            user.getEmail(), user.getDisplayName(), user.getCurrentStreak());
      } catch (Exception e) {
        log.error("Error processing streak risk for user: {}", user.getId(), e);
      }
    }
    log.info("Streak Risk check job finished.");
  }

  /** Cảnh báo Churn - Bỏ học lâu (Chạy lúc 20:00 hàng ngày) */
  @Scheduled(cron = "0 0 20 * * *")
  @Transactional
  public void checkChurnAlert() {
    log.info("⏰ Starting Churn Alert check job...");
    LocalDate today = LocalDate.now();

    // Lọc các mốc 3 ngày và 7 ngày
    sendChurnAlertForDays(today.minusDays(3), 3);
    sendChurnAlertForDays(today.minusDays(7), 7);

    log.info("Churn Alert check job finished.");
  }

  private void sendChurnAlertForDays(LocalDate targetDate, long daysInactive) {
    List<User> users = userRepository.findByLastStudyDate(targetDate);
    log.info("Found {} users inactive for {} days.", users.size(), daysInactive);

    for (User user : users) {
      try {
        // Lấy target score từ profile
        int targetScore =
            userLearningProfileRepository
                .findByUserId(user.getId())
                .map(UserLearningProfile::getTargetScore)
                .orElse(0);

        // Tạo Notification
        Notification notification =
            Notification.builder()
                .title("✨ Todaii English nhớ bạn!")
                .content(
                    "Đã "
                        + daysInactive
                        + " ngày rồi bạn chưa học. Quay lại luyện tập để không quên kiến thức nhé!")
                .type(NotificationType.CHURN_ALERT)
                .user(user)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Gửi email
        smtpService.sendChurnAlertEmail(
            user.getEmail(), user.getDisplayName(), targetScore, daysInactive);
      } catch (Exception e) {
        log.error("Error processing churn alert for user: {}", user.getId(), e);
      }
    }
  }

  /** Cảnh báo Countdown Kì Thi (Chạy lúc 08:00 mỗi sáng) */
  @Scheduled(cron = "0 0 8 * * *")
  @Transactional
  public void checkExamCountdown() {
    log.info("⏰ Starting Exam Countdown check job...");
    LocalDate today = LocalDate.now();
    List<Integer> milestones = List.of(7, 5, 3, 1);

    for (int milestone : milestones) {
      LocalDate targetExamDate = today.plusDays(milestone);
      List<UserLearningProfile> profiles =
          userLearningProfileRepository.findByExamDate(targetExamDate);
      log.info(
          "Found {} profiles with exam date in {} days ({}).",
          profiles.size(),
          milestone,
          targetExamDate);

      for (UserLearningProfile profile : profiles) {
        try {
          User user = profile.getUser();
          int targetScore = profile.getTargetScore();

          // Tạo Notification
          Notification notification =
              Notification.builder()
                  .title("⏰ Đếm ngược ngày thi TOEIC!")
                  .content(
                      "Chỉ còn "
                          + milestone
                          + " ngày nữa là đến kì thi của bạn rồi! Hãy tập trung ôn luyện và thi thử"
                          + " nhé!")
                  .type(NotificationType.EXAM_COUNTDOWN)
                  .user(user)
                  .isRead(false)
                  .build();
          notificationRepository.save(notification);

          // Gửi email
          smtpService.sendExamCountdownEmail(
              user.getEmail(), user.getDisplayName(), targetScore, milestone);
        } catch (Exception e) {
          log.error("Error processing exam countdown for profile: {}", profile.getId(), e);
        }
      }
    }
    log.info("Exam Countdown check job finished.");
  }
}
