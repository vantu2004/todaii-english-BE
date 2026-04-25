package com.todaii.english.server.toeic_question;

import com.todaii.english.core.entity.ToeicQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<ToeicQuestion, Long> {
    Page<ToeicQuestion> findByTestId(Long testId, Pageable pageable);

    Page<ToeicQuestion> findByGroupId(Long groupId, Pageable pageable);
}
