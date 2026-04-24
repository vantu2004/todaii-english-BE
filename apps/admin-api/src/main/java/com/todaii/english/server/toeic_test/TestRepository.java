package com.todaii.english.server.toeic_test;

import com.todaii.english.core.entity.ToeicTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<ToeicTest, Long> {
}
