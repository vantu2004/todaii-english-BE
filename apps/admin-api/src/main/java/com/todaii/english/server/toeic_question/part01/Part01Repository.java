package com.todaii.english.server.toeic_question.part01;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.ToeicQuestion;

@Repository
public interface Part01Repository extends JpaRepository<ToeicQuestion, Long> {
  List<ToeicQuestion> findByTestIdAndPartNumber(Long testId, Integer partNumber);
}
