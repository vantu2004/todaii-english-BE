package com.todaii.english.server.toeic_question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.ToeicQuestion;

@Repository
public interface QuestionRepository
    extends JpaRepository<ToeicQuestion, Long>, JpaSpecificationExecutor<ToeicQuestion> {}
