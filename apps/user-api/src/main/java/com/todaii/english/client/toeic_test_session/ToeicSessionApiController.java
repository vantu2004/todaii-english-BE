package com.todaii.english.client.toeic_test_session;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.core.entity.toeic.ToeicUserAnswer;
import com.todaii.english.shared.request.client.AnswerRequest;
import com.todaii.english.shared.request.client.StartSessionRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/session")
public class ToeicSessionApiController {
  private final ToeicTestSessionService sessionService;

  @GetMapping("/{sessionId}")
  public ResponseEntity<ToeicTestSession> getSessionDetails(
      Authentication authentication, @PathVariable Long sessionId) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.getToeicTestSession(currentUserId, sessionId));
  }

  @GetMapping("/history")
  public ResponseEntity<List<ToeicTestSession>> getSessionHistory(Authentication authentication) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.getSessionHistory(currentUserId));
  }

  @PostMapping("/start")
  public ResponseEntity<ToeicTestSession> startSession(
      Authentication authentication, @Valid @RequestBody StartSessionRequest request) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.startSession(currentUserId, request));
  }

  // hỗ trợ lưu session cho user khi chưa hoàn thành
  @PostMapping("/{sessionId}/answers")
  public ResponseEntity<List<ToeicUserAnswer>> saveAnswers(
      Authentication authentication,
      @PathVariable Long sessionId,
      @RequestBody List<@Valid AnswerRequest> requests) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.saveAnswers(currentUserId, sessionId, requests));
  }

  @PostMapping("/{sessionId}/submit")
  public ResponseEntity<ToeicTestSession> submitSession(
      Authentication authentication,
      @PathVariable Long sessionId,
      @RequestBody List<@Valid AnswerRequest> requests) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.submitSession(currentUserId, sessionId, requests));
  }
}
