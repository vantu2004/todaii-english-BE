package com.todaii.english.client.toeic_tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTag;

@Repository
public interface TagRepository extends JpaRepository<ToeicTag, Long> {
  @Query(
      """
    SELECT DISTINCT t
    FROM ToeicQuestion q
    JOIN q.tags t
    WHERE q.test.id = :testId
    ORDER BY t.name
""")
  List<ToeicTag> findAllTagsByTestId(Long testId);

  @Query(
      """
    SELECT q
    FROM ToeicQuestion q
    JOIN q.tags t
    WHERE q.test.id = :testId
      AND t.id = :tagId
    ORDER BY q.createdAt
""")
  List<ToeicQuestion> findQuestionsByTag(Long testId, Long tagId);
}
