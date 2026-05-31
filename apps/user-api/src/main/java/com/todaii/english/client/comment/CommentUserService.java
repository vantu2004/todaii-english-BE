package com.todaii.english.client.comment;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.article.ArticleRepository;
import com.todaii.english.client.toeic_test.TestRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.client.video.VideoRepository;
import com.todaii.english.core.entity.Comment;
import com.todaii.english.core.entity.CommentReport;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.dto.CommentDTO;
import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.client.CreateCommentRequest;
import com.todaii.english.shared.request.client.ReportCommentRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentUserService {
  private final CommentRepository commentRepository;
  private final CommentReportRepository reportRepository;
  private final UserRepository userRepository;
  private final TestRepository testRepository;
  private final ArticleRepository articleRepository;
  private final VideoRepository videoRepository;

  @Transactional(readOnly = true)
  public List<CommentDTO> getCommentTree(CommentTargetType targetType, Long targetId) {
    List<Comment> comments =
        commentRepository.findByTargetTypeAndTargetIdAndStatusOrderByCreatedAtAsc(
            targetType, targetId, CommentStatus.APPROVED);

    Map<Long, CommentDTO> dtoMap = new LinkedHashMap<>();
    for (Comment c : comments) {
      dtoMap.put(c.getId(), mapToCommentDTO(c));
    }

    List<CommentDTO> rootComments = new ArrayList<>();
    for (CommentDTO dto : dtoMap.values()) {
      if (dto.getParentId() != null) {
        CommentDTO parent = dtoMap.get(dto.getParentId());
        if (parent != null) {
          parent.getReplies().add(dto);
        }
      } else {
        rootComments.add(dto);
      }
    }

    return rootComments;
  }

  @Transactional
  public CommentDTO createComment(Long userId, CreateCommentRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(404, "User not found"));

    // Validate target exists
    if (request.getTargetType() == CommentTargetType.TOEIC_TEST) {
      testRepository
          .findById(request.getTargetId())
          .orElseThrow(() -> new BusinessException(404, "TOEIC test not found"));
    } else if (request.getTargetType() == CommentTargetType.ARTICLE) {
      articleRepository
          .findById(request.getTargetId())
          .orElseThrow(() -> new BusinessException(404, "Article not found"));
    } else if (request.getTargetType() == CommentTargetType.VIDEO) {
      videoRepository
          .findById(request.getTargetId())
          .orElseThrow(() -> new BusinessException(404, "Video not found"));
    } else {
      throw new BusinessException(400, "Unsupported comment target type");
    }

    Comment parent = null;
    if (request.getParentId() != null) {
      parent =
          commentRepository
              .findById(request.getParentId())
              .orElseThrow(() -> new BusinessException(404, "Parent comment not found"));

      if (!parent.getTargetType().equals(request.getTargetType())
          || !parent.getTargetId().equals(request.getTargetId())) {
        throw new BusinessException(400, "Parent comment does not belong to this target");
      }

      if (parent.getStatus() != CommentStatus.APPROVED) {
        throw new BusinessException(400, "Cannot reply to a hidden or pending comment");
      }
    }

    Comment comment =
        Comment.builder()
            .user(user)
            .targetType(request.getTargetType())
            .targetId(request.getTargetId())
            .parent(parent)
            .content(request.getContent())
            .status(CommentStatus.APPROVED)
            .isAutoFlagged(false)
            .build();

    comment = commentRepository.save(comment);
    return mapToCommentDTO(comment);
  }

  @Transactional
  public void deleteComment(Long userId, Long commentId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new BusinessException(404, "Comment not found"));

    if (comment.getUser() == null || !comment.getUser().getId().equals(userId)) {
      throw new BusinessException(403, "You do not have permission to delete this comment");
    }

    // Delete reports first to avoid foreign key violation
    reportRepository.deleteByCommentId(commentId);

    commentRepository.delete(comment);
  }

  @Transactional
  public void reportComment(Long userId, Long commentId, ReportCommentRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(404, "User not found"));

    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new BusinessException(404, "Comment not found"));

    if (reportRepository.existsByCommentIdAndReportedById(commentId, userId)) {
      throw new BusinessException(400, "You have already reported this comment");
    }

    CommentReport report =
        CommentReport.builder()
            .comment(comment)
            .reportedBy(user)
            .reason(request.getReason())
            .build();

    reportRepository.save(report);
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
        .replies(new ArrayList<>()) // replies mapped in tree builder
        .build();
  }
}
