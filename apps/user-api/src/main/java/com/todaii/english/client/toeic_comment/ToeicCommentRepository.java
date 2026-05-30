package com.todaii.english.client.toeic_comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicComment;

@Repository
public interface ToeicCommentRepository extends JpaRepository<ToeicComment, Long> {}
