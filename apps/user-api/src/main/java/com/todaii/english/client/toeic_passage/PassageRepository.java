package com.todaii.english.client.toeic_passage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicPassage;

@Repository
public interface PassageRepository extends JpaRepository<ToeicPassage, Long> {
  List<ToeicPassage> findByTestIdAndPartNumber(Long testId, Integer partNumber);
}
