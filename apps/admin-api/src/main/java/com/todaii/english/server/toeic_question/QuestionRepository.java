package com.todaii.english.server.toeic_question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicQuestion;

@Repository
public interface QuestionRepository extends JpaRepository<ToeicQuestion, Long> {
  List<ToeicQuestion> findAllByTagsId(Long id);

  List<ToeicQuestion> findByTestIdAndPartNumber(Long testId, Integer partNumber);
}
