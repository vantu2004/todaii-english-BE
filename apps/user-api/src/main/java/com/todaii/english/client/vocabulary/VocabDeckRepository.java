package com.todaii.english.client.vocabulary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabDeck;

@Repository
public interface VocabDeckRepository extends JpaRepository<VocabDeck, Long>, JpaSpecificationExecutor<VocabDeck> {
	@Query("SELECT v FROM VocabDeck v WHERE v.enabled = true AND v.id = ?1")
	public Optional<VocabDeck> findById(Long id);
}
