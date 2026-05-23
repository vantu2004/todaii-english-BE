package com.todaii.english.client.toeic_test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicTest;

@Repository
public interface TestRepository extends JpaRepository<ToeicTest, Long> {
}
