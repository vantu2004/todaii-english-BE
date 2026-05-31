package com.todaii.english.client.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.core.entity.CommentReport;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
  boolean existsByCommentIdAndReportedById(Long commentId, Long reportedById);

  @Modifying
  @Transactional
  @Query("DELETE FROM CommentReport r WHERE r.comment.id = :commentId")
  void deleteByCommentId(@Param("commentId") Long commentId);
}
