package com.todaii.english.client.toeic_test_session;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.shared.request.client.StartSessionRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/session")
public class ToeicSessionApiController {
  private final ToeicTestSessionService sessionService;

  @PostMapping("/start")
  public ResponseEntity<ToeicTestSession> startSession(
      Authentication authentication, @Valid @RequestBody StartSessionRequest request) {
    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
    Long currentUserId = principal.getUser().getId();

    return ResponseEntity.ok(sessionService.startSession(currentUserId, request));
  }

  //  @PostMapping("/{sessionId}/answers")
  //  public ResponseEntity<List<ToeicUserAnswerDTO>> saveAnswers(
  //      Authentication authentication,
  //      @PathVariable Long sessionId,
  //      @RequestBody List<@Valid AnswerRequest> requests) {
  //    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
  //    Long currentUserId = principal.getUser().getId();
  //    return ResponseEntity.ok(sessionService.saveAnswers(currentUserId, sessionId, requests));
  //  }
  //
  //  @PostMapping("/{sessionId}/question/{questionId}/mark")
  //  public ResponseEntity<ToeicUserAnswerDTO> toggleMark(
  //      Authentication authentication,
  //      @PathVariable Long sessionId,
  //      @PathVariable Long questionId,
  //      @RequestParam Boolean isMarked) {
  //    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
  //    Long currentUserId = principal.getUser().getId();
  //    return ResponseEntity.ok(
  //        sessionService.toggleMark(currentUserId, sessionId, questionId, isMarked));
  //  }
  //
  //  @PostMapping("/{sessionId}/submit")
  //  public ResponseEntity<ToeicTestSessionDTO> submitSession(
  //      Authentication authentication,
  //      @PathVariable Long sessionId,
  //      @RequestBody(required = false) SubmitSessionRequest request) {
  //    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
  //    Long currentUserId = principal.getUser().getId();
  //    return ResponseEntity.ok(sessionService.submitSession(currentUserId, sessionId, request));
  //  }
  //
  //  @GetMapping("/{sessionId}")
  //  public ResponseEntity<ToeicSessionDetailsResponse> getSessionDetails(
  //      Authentication authentication, @PathVariable Long sessionId) {
  //    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
  //    Long currentUserId = principal.getUser().getId();
  //    return ResponseEntity.ok(sessionService.getSessionDetails(currentUserId, sessionId));
  //  }
  //
  //  @GetMapping("/history")
  //  public ResponseEntity<PagedResponse<ToeicTestSessionDTO>> getSessionHistory(
  //      Authentication authentication,
  //      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
  //          int page,
  //      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
  //          int size) {
  //    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
  //    Long currentUserId = principal.getUser().getId();
  //    return ResponseEntity.ok(sessionService.getSessionHistory(currentUserId, page, size));
  //  }
}
