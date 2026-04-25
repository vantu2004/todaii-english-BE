package com.todaii.english.server.toeic_question_group;

import com.todaii.english.core.entity.ToeicQuestionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionGroupRepository extends JpaRepository<ToeicQuestionGroup, Long> {
    Page<ToeicQuestionGroup> findByTestId(Long testId, Pageable pageable);
}
