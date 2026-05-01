package com.todaii.english.server.toeic_passage;

import com.todaii.english.core.entity.ToeicPassage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassageRepository extends JpaRepository<ToeicPassage, Long> {
    Page<ToeicPassage> findByTestId(Long testId, Pageable pageable);
}
