package com.todaii.english.client.learning.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.learning.repository.NotificationRepository;
import com.todaii.english.core.entity.learning.Notification;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;

  public List<Notification> getNotifications(Long userId) {
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }

  public long getUnreadCount(Long userId) {
    return notificationRepository.countByUserIdAndIsReadFalse(userId);
  }

  @Transactional
  public void markAsRead(Long notificationId, Long userId) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new BusinessException(404, "Notification not found"));

    if (!notification.getUser().getId().equals(userId)) {
      throw new BusinessException(403, "Access denied");
    }

    notification.setIsRead(true);

    notificationRepository.save(notification);
  }
}
