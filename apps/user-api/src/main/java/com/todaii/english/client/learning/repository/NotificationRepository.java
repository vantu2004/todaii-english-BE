package com.todaii.english.client.learning.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.Notification;
import com.todaii.english.shared.enums.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

  long countByUserIdAndIsReadFalse(Long userId);

  long countByUserIdAndTypeAndCreatedAtAfter(
      Long userId, NotificationType type, LocalDateTime createdAt);
}
