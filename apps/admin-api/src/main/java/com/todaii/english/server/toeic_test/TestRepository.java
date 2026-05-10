package com.todaii.english.server.toeic_test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicTest;

@Repository
public interface TestRepository extends JpaRepository<ToeicTest, Long> {
  @Query(
      """
                SELECT DISTINCT t FROM ToeicTest t
                WHERE
                        :keyword IS NULL
                        OR STR(t.id) LIKE CONCAT('%', :keyword, '%')
                        OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(t.testType) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(t.status) LIKE LOWER(CONCAT('%', :keyword, '%'))

                """)
  Page<ToeicTest> getAllPaged(String keyword, Pageable pageable);
}
