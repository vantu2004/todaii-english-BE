package com.todaii.english.client.toeic_test_session;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.shared.dto.toeic.ToeicTestSessionDTO;
import com.todaii.english.shared.dto.toeic.ToeicUserAnswerDTO;
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
  public ResponseEntity<ToeicTestSessionDTO> getSessionDetails(
      Authentication authentication, @PathVariable Long sessionId) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.getSessionDetailsDto(currentUserId, sessionId));
  }

  @GetMapping("/history")
  public ResponseEntity<List<ToeicTestSessionDTO>> getSessionHistory(
      Authentication authentication) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.getSessionHistory(currentUserId));
  }

  @PostMapping("/start")
  public ResponseEntity<ToeicTestSessionDTO> startSession(
      Authentication authentication, @Valid @RequestBody StartSessionRequest request) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.startSession(currentUserId, request));
  }

  @PostMapping("/{sessionId}/answers")
  public ResponseEntity<List<ToeicUserAnswerDTO>> saveAnswers(
      Authentication authentication,
      @PathVariable Long sessionId,
      @RequestBody List<@Valid AnswerRequest> requests) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.saveAnswers(currentUserId, sessionId, requests));
  }

  @PostMapping("/{sessionId}/submit")
  public ResponseEntity<ToeicTestSessionDTO> submitSession(
      Authentication authentication,
      @PathVariable Long sessionId,
      @RequestBody(required = false) List<@Valid AnswerRequest> requests) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(sessionService.submitSession(currentUserId, sessionId, requests));
  }
}
