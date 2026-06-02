package com.todaii.english.client.toeic_user_highlight;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.shared.dto.toeic.ToeicUserHighlightDTO;
import com.todaii.english.shared.request.client.ToeicHighlightRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/highlights")
public class ToeicUserHighlightController {
  private final ToeicUserHighlightService highlightService;

  @PostMapping
  public ResponseEntity<ToeicUserHighlightDTO> createHighlight(
      Authentication authentication, @Valid @RequestBody ToeicHighlightRequest request) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    ToeicUserHighlightDTO highlightDTO = highlightService.createHighlight(currentUserId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(highlightDTO);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ToeicUserHighlightDTO> updateHighlight(
      Authentication authentication,
      @PathVariable Long id,
      @RequestBody ToeicHighlightRequest request) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    ToeicUserHighlightDTO highlightDTO =
        highlightService.updateHighlight(currentUserId, id, request);
    return ResponseEntity.ok(highlightDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteHighlight(
      Authentication authentication, @PathVariable Long id) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    highlightService.deleteHighlight(currentUserId, id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/session/{sessionId}")
  public ResponseEntity<List<ToeicUserHighlightDTO>> getHighlightsBySession(
      Authentication authentication, @PathVariable Long sessionId) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    return ResponseEntity.ok(highlightService.getHighlightsBySession(currentUserId, sessionId));
  }
}
