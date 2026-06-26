package com.todaii.english.client.toeic_test;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicTest;

@Repository
public interface TestRepository extends JpaRepository<ToeicTest, Long> {
  @Query(
      """
      SELECT DISTINCT t FROM ToeicTest t
      WHERE t.status = com.todaii.english.shared.enums.TestStatus.PUBLISHED
      AND (
          :keyword IS NULL
          OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      """)
  Page<ToeicTest> getAllPublishedPaged(@Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
      SELECT t FROM ToeicTest t
      WHERE t.collection.id = :collectionId
      AND t.status = com.todaii.english.shared.enums.TestStatus.PUBLISHED
      AND (
          :keyword IS NULL
          OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      """)
  Page<ToeicTest> findPublishedByCollectionId(
      @Param("collectionId") Long collectionId,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query(
      """
      SELECT t FROM ToeicTest t
      WHERE t.id = :id AND t.status = com.todaii.english.shared.enums.TestStatus.PUBLISHED
      """)
  Optional<ToeicTest> findPublishedById(@Param("id") Long id);

  // Recommend: Lấy ngẫu nhiên N test PUBLISHED có chứa câu hỏi thuộc part chỉ định
  @Query(
      value =
          "SELECT DISTINCT t.* FROM toeic_tests t JOIN toeic_questions q ON q.test_id = t.id"
              + " WHERE t.status = 'PUBLISHED' AND q.part_number = :partNumber ORDER BY RAND()"
              + " LIMIT :limit",
      nativeQuery = true)
  List<ToeicTest> findRandomPublishedByPartNumber(
      @Param("partNumber") int partNumber, @Param("limit") int limit);

  // Recommend fallback: Lấy ngẫu nhiên N test PUBLISHED bất kỳ
  @Query(
      value =
          "SELECT * FROM toeic_tests t WHERE t.status = 'PUBLISHED' ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<ToeicTest> findRandomPublished(@Param("limit") int limit);
}
