package com.todaii.english.server.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.core.entity.CommentReport;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
  @Query("""
      SELECT r FROM CommentReport r
      WHERE (:commentId IS NULL OR r.comment.id = :commentId)
        AND (:keyword IS NULL OR LOWER(r.reason) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(r.comment.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(r.reportedBy.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(r.reportedBy.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
  """)
  Page<CommentReport> searchReports(
      @Param("commentId") Long commentId,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Modifying
  @Transactional
  @Query("DELETE FROM CommentReport r WHERE r.comment.id = :commentId")
  void deleteByCommentId(@Param("commentId") Long commentId);
}
