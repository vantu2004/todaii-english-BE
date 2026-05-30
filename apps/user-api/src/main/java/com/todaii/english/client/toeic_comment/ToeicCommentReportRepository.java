package com.todaii.english.client.toeic_comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicCommentReport;

@Repository
public interface ToeicCommentReportRepository extends JpaRepository<ToeicCommentReport, Long> {}
