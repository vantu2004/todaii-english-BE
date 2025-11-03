package com.todaii.english.server.vocabulary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabDeck;

@Repository
public interface VocabDeckRepository extends JpaRepository<VocabDeck, Long> {

}
