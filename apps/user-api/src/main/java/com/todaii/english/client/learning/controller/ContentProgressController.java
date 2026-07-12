package com.todaii.english.client.learning.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.ContentProgressService;
import com.todaii.english.shared.enums.TopicType;
import com.todaii.english.shared.request.client.UpsertProgressRequest;
import com.todaii.english.shared.response.ContentProgressResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Validated
public class ContentProgressController {
  private final ContentProgressService contentProgressService;

  @GetMapping("/{contentId}")
  public ResponseEntity<ContentProgressResponse> getProgress(
      Authentication authentication,
      @PathVariable Long contentId,
      @RequestParam(name = "type") @NotNull(message = "Type must not be null") TopicType type) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    ContentProgressResponse response = contentProgressService.getProgress(userId, contentId, type);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{contentId}")
  public ResponseEntity<ContentProgressResponse> upsertProgress(
      Authentication authentication,
      @PathVariable Long contentId,
      @Valid @RequestBody UpsertProgressRequest request) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    ContentProgressResponse response =
        contentProgressService.upsertProgress(userId, contentId, request);

    return ResponseEntity.ok(response);
  }
}
