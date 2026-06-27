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
import com.todaii.english.shared.enums.UserStatus;

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
    List<User> users =
        userRepository.findByCurrentStreakGreaterThanAndLastStudyDateBefore(0, today);

    log.info("Found {} users at risk.", users.size());

    for (User user : users) {
      try {
        if (!isEligibleForNotification(user)) {
          continue;
        }

        // Đã gửi bao nhiêu lần kể từ lần học gần nhất
        long sentCount =
            notificationRepository.countByUserIdAndTypeAndCreatedAtAfter(
                user.getId(), NotificationType.STREAK_RISK, user.getLastStudyDate().atStartOfDay());

        if (sentCount >= 3) {
          continue;
        }

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

        smtpService.sendStreakRiskEmail(
            user.getEmail(), user.getDisplayName(), user.getCurrentStreak());

      } catch (Exception e) {
        log.error("Error processing streak risk for user {}", user.getId(), e);
      }
    }

    log.info("✅ Streak Risk finished.");
  }

  /** Cảnh báo Countdown Kì Thi (Chạy lúc 08:00 mỗi sáng) */
  @Scheduled(cron = "0 0 8 * * *")
  @Transactional
  public void checkExamCountdown() {
    log.info("⏰ Starting Exam Countdown job...");

    LocalDate today = LocalDate.now();
    List<Integer> milestones = List.of(7, 5, 3, 1);

    for (int milestone : milestones) {
      LocalDate targetExamDate = today.plusDays(milestone);
      List<UserLearningProfile> profiles =
          userLearningProfileRepository.findByExamDate(targetExamDate);

      log.info("Found {} profiles for {}-day reminder.", profiles.size(), milestone);

      for (UserLearningProfile profile : profiles) {
        try {
          if (profile == null) {
            continue;
          }

          User user = profile.getUser();

          if (!isEligibleForNotification(user)) {
            continue;
          }

          if (profile.getExamDate() == null
              || profile.getTargetScore() == null
              || profile.getTargetScore() <= 0) {
            continue;
          }

          Notification notification =
              Notification.builder()
                  .title("⏰ Đếm ngược ngày thi TOEIC!")
                  .content(
                      "Chỉ còn "
                          + milestone
                          + " ngày nữa là đến kỳ thi của bạn rồi! Hãy tập trung ôn luyện và thi thử"
                          + " nhé!")
                  .type(NotificationType.EXAM_COUNTDOWN)
                  .user(user)
                  .isRead(false)
                  .build();

          notificationRepository.save(notification);

          smtpService.sendExamCountdownEmail(
              user.getEmail(), user.getDisplayName(), profile.getTargetScore(), milestone);

        } catch (Exception e) {

          log.error("Error processing exam countdown for profile {}", profile.getId(), e);
        }
      }
    }

    log.info("✅ Exam Countdown finished.");
  }

  private boolean isEligibleForNotification(User user) {
    return user != null
        && !Boolean.TRUE.equals(user.getIsDeleted())
        && Boolean.TRUE.equals(user.getEnabled())
        && user.getStatus() == UserStatus.ACTIVE;
  }
}
