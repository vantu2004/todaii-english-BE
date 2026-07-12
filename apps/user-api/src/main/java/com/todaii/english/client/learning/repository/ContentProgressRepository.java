package com.todaii.english.client.learning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.ContentProgress;
import com.todaii.english.shared.enums.TopicType;

@Repository
public interface ContentProgressRepository extends JpaRepository<ContentProgress, Long> {
  Optional<ContentProgress> findByUserIdAndContentIdAndContentType(
      Long userId, Long contentId, TopicType contentType);
}
