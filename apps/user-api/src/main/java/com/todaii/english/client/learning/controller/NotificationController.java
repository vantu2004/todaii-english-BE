package com.todaii.english.client.learning.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.NotificationService;
import com.todaii.english.core.entity.learning.Notification;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<Notification>> getNotifications(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    List<Notification> notifications = notificationService.getNotifications(userId);
    return ResponseEntity.ok(notifications);
  }

  @PutMapping("/{id}/read")
  public ResponseEntity<Void> markAsRead(
      Authentication authentication, @PathVariable("id") Long id) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    notificationService.markAsRead(id, userId);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/unread-count")
  public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    long count = notificationService.getUnreadCount(userId);

    return ResponseEntity.ok(Map.of("unreadCount", count));
  }
}
