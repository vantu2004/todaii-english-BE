package com.todaii.english.server.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.core.entity.Comment;
import com.todaii.english.core.entity.CommentReport;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.dto.CommentDTO;
import com.todaii.english.shared.dto.CommentReportDTO;
import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentAdminService {
  private final CommentRepository commentRepository;
  private final CommentReportRepository reportRepository;

  @Transactional(readOnly = true)
  public Page<CommentDTO> searchComments(
      CommentTargetType targetType,
      Long targetId,
      CommentStatus status,
      Boolean isAutoFlagged,
      int page,
      int size,
      String sortBy,
      String direction,
      String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<Comment> commentPage =
        commentRepository.searchComments(
            targetType, targetId, status, isAutoFlagged, AdminUtils.formatSearchKeyword(keyword), pageable);

    return commentPage.map(this::mapToCommentDTO);
  }

  @Transactional
  public CommentDTO updateCommentStatus(Long commentId, CommentStatus status) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new BusinessException(404, "Comment not found"));

    comment.setStatus(status);
    comment = commentRepository.save(comment);
    return mapToCommentDTO(comment);
  }

  @Transactional
  public void deleteComment(Long commentId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new BusinessException(404, "Comment not found"));

    // Delete associated reports first to avoid FK constraint violation
    reportRepository.deleteByCommentId(commentId);

    commentRepository.delete(comment);
  }

  @Transactional(readOnly = true)
  public Page<CommentReportDTO> searchReports(
      Long commentId, int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<CommentReport> reportPage =
        reportRepository.searchReports(
            commentId, AdminUtils.formatSearchKeyword(keyword), pageable);

    return reportPage.map(this::mapToReportDTO);
  }

  @Transactional
  public void deleteReport(Long reportId) {
    CommentReport report =
        reportRepository
            .findById(reportId)
            .orElseThrow(() -> new BusinessException(404, "Report not found"));

    reportRepository.delete(report);
  }

  private CommentDTO mapToCommentDTO(Comment comment) {
    if (comment == null) return null;
    return CommentDTO.builder()
        .id(comment.getId())
        .targetType(comment.getTargetType())
        .targetId(comment.getTargetId())
        .userId(comment.getUser() != null ? comment.getUser().getId() : null)
        .userDisplayName(comment.getUser() != null ? comment.getUser().getDisplayName() : null)
        .userAvatarUrl(comment.getUser() != null ? comment.getUser().getAvatarUrl() : null)
        .adminId(comment.getAdmin() != null ? comment.getAdmin().getId() : null)
        .adminDisplayName(comment.getAdmin() != null ? comment.getAdmin().getDisplayName() : null)
        .adminAvatarUrl(comment.getAdmin() != null ? comment.getAdmin().getAvatarUrl() : null)
        .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
        .content(comment.getContent())
        .status(comment.getStatus())
        .isAutoFlagged(comment.getIsAutoFlagged())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }

  private CommentReportDTO mapToReportDTO(CommentReport report) {
    if (report == null) return null;
    return CommentReportDTO.builder()
        .id(report.getId())
        .commentId(report.getComment().getId())
        .commentContent(report.getComment().getContent())
        .commentUserDisplayName(
            report.getComment().getUser() != null
                ? report.getComment().getUser().getDisplayName()
                : (report.getComment().getAdmin() != null
                    ? report.getComment().getAdmin().getDisplayName()
                    : "Unknown"))
        .reportedById(report.getReportedBy().getId())
        .reportedByDisplayName(report.getReportedBy().getDisplayName())
        .reason(report.getReason())
        .createdAt(report.getCreatedAt())
        .build();
  }
}
