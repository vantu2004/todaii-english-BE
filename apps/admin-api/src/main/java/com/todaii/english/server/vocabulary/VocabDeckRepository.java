package com.todaii.english.server.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabDeck;

@Repository
public interface VocabDeckRepository extends JpaRepository<VocabDeck, Long> {
	@Query("""
			SELECT d FROM VocabDeck d
			WHERE
			    ?1 IS NULL
			    OR STR(d.id) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(d.name) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR d.description LIKE CONCAT('%', ?1, '%')
			    OR LOWER(d.cefrLevel) LIKE LOWER(CONCAT('%', ?1, '%'))
			""")
	public Page<VocabDeck> search(String keyword, Pageable pageable);
}
