package com.todaii.english.client.learning.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.UserLearningProfileService;
import com.todaii.english.shared.dto.learning.UserLearningProfileDTO;
import com.todaii.english.shared.request.client.UpdateLearningProfileRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/learning-profile")
@RequiredArgsConstructor
public class UserLearningProfileController {
  private final UserLearningProfileService userLearningProfileService;

  @GetMapping
  public ResponseEntity<UserLearningProfileDTO> getProfile(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(userLearningProfileService.getProfile(userId));
  }

  @PutMapping
  public ResponseEntity<UserLearningProfileDTO> updateProfile(
      Authentication authentication,
      @Valid @RequestBody UpdateLearningProfileRequest request) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(userLearningProfileService.updateProfile(userId, request));
  }
}
