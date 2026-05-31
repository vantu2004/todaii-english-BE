package com.todaii.english.client.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Comment;
import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByTargetTypeAndTargetIdAndStatusOrderByCreatedAtAsc(
      CommentTargetType targetType, Long targetId, CommentStatus status);
}
