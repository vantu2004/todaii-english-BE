package com.todaii.english.server.comment;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.dto.CommentDTO;
import com.todaii.english.shared.dto.CommentReportDTO;
import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;
import com.todaii.english.shared.response.PagedResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
public class CommentAdminController {
  private final CommentAdminService commentAdminService;

  @GetMapping("/comments")
  public ResponseEntity<PagedResponse<CommentDTO>> searchComments(
      @RequestParam(required = false) CommentTargetType targetType,
      @RequestParam(required = false) Long targetId,
      @RequestParam(required = false) CommentStatus status,
      @RequestParam(required = false) Boolean isAutoFlagged,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String keyword) {

    Page<CommentDTO> results =
        commentAdminService.searchComments(
            targetType, targetId, status, isAutoFlagged, page, size, sortBy, direction, keyword);

    PagedResponse<CommentDTO> response =
        new PagedResponse<>(
            results.getContent(),
            page,
            size,
            results.getTotalElements(),
            results.getTotalPages(),
            results.isFirst(),
            results.isLast(),
            sortBy,
            direction);

    return ResponseEntity.ok(response);
  }

  @PatchMapping("/comments/{id}/status")
  public ResponseEntity<CommentDTO> updateCommentStatus(
      @PathVariable Long id, @RequestParam CommentStatus status) {
    return ResponseEntity.ok(commentAdminService.updateCommentStatus(id, status));
  }

  @DeleteMapping("/comments/{id}")
  public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
    commentAdminService.deleteComment(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/reports")
  public ResponseEntity<PagedResponse<CommentReportDTO>> searchReports(
      @RequestParam(required = false) Long commentId,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String keyword) {

    Page<CommentReportDTO> results =
        commentAdminService.searchReports(commentId, page, size, sortBy, direction, keyword);

    PagedResponse<CommentReportDTO> response =
        new PagedResponse<>(
            results.getContent(),
            page,
            size,
            results.getTotalElements(),
            results.getTotalPages(),
            results.isFirst(),
            results.isLast(),
            sortBy,
            direction);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/reports/{id}")
  public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
    commentAdminService.deleteReport(id);
    return ResponseEntity.noContent().build();
  }
}
