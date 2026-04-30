package com.todaii.english.server.toeic_tag;

import com.todaii.english.core.entity.ToeicTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<ToeicTag, Long> {
}
