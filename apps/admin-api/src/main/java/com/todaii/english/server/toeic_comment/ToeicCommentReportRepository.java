package com.todaii.english.server.toeic_comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicCommentReport;

@Repository
public interface ToeicCommentReportRepository extends JpaRepository<ToeicCommentReport, Long> {}
