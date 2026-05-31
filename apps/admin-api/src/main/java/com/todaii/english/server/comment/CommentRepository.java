package com.todaii.english.server.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Comment;
import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  @Query(
      """
      SELECT c FROM Comment c
      WHERE (:targetType IS NULL OR c.targetType = :targetType)
        AND (:targetId IS NULL OR c.targetId = :targetId)
        AND (:status IS NULL OR c.status = :status)
        AND (:isAutoFlagged IS NULL OR c.isAutoFlagged = :isAutoFlagged)
        AND (:keyword IS NULL OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(c.user.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(c.user.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
  """)
  Page<Comment> searchComments(
      @Param("targetType") CommentTargetType targetType,
      @Param("targetId") Long targetId,
      @Param("status") CommentStatus status,
      @Param("isAutoFlagged") Boolean isAutoFlagged,
      @Param("keyword") String keyword,
      Pageable pageable);
}
