package com.todaii.english.server.toeic_tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicTag;

@Repository
public interface TagRepository extends JpaRepository<ToeicTag, Long> {
  boolean existsByAlias(String alias);
}
