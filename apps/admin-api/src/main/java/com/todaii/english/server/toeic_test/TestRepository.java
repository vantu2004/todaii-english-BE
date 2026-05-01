package com.todaii.english.server.toeic_test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.ToeicTest;

@Repository
public interface TestRepository extends JpaRepository<ToeicTest, Long> {
  Page<ToeicTest> findByCollectionId(Long collectionId, Pageable pageable);
}
