package com.todaii.english.client.comment;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.shared.dto.CommentDTO;
import com.todaii.english.shared.enums.CommentTargetType;
import com.todaii.english.shared.request.client.CreateCommentRequest;
import com.todaii.english.shared.request.client.ReportCommentRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/comments")
public class CommentUserController {
  private final CommentUserService commentUserService;

  @GetMapping
  public ResponseEntity<List<CommentDTO>> getCommentTree(
      @RequestParam CommentTargetType targetType,
      @RequestParam Long targetId) {
    return ResponseEntity.ok(commentUserService.getCommentTree(targetType, targetId));
  }

  @PostMapping
  public ResponseEntity<CommentDTO> createComment(
      Authentication authentication, @Valid @RequestBody CreateCommentRequest request) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    CommentDTO commentDTO = commentUserService.createComment(currentUserId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComment(
      Authentication authentication, @PathVariable Long id) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    commentUserService.deleteComment(currentUserId, id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/report")
  public ResponseEntity<Void> reportComment(
      Authentication authentication,
      @PathVariable Long id,
      @Valid @RequestBody ReportCommentRequest request) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);
    commentUserService.reportComment(currentUserId, id, request);
    return ResponseEntity.ok().build();
  }
}
