package com.todaii.english.client.toeic_user_highlight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicUserHighlight;

@Repository
public interface ToeicUserHighlightRepository extends JpaRepository<ToeicUserHighlight, Long> {}
