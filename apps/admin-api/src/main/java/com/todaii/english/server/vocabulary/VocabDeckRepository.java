package com.todaii.english.server.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabDeck;

@Repository
public interface VocabDeckRepository extends JpaRepository<VocabDeck, Long> {
	/*
	 * nếu groupId != null nghĩa là tìm các deck dựa theo groupId, dùng chung cho 2
	 * hàm getDecks
	 */
	@Query("""
			SELECT DISTINCT d FROM VocabDeck d
			LEFT JOIN d.groups g
			WHERE
			    (?1 IS NULL OR g.id = ?1) AND
			    (
			        ?2 IS NULL
			        OR STR(d.id) LIKE CONCAT('%',  ?2, '%')
			        OR LOWER(d.name) LIKE LOWER(CONCAT('%', ?2, '%'))
			        OR d.description LIKE CONCAT('%', ?2, '%')
			        OR LOWER(d.cefrLevel) LIKE LOWER(CONCAT('%', ?2, '%'))
			    )
			""")
	public Page<VocabDeck> search(Long groupId, String keyword, Pageable pageable);
}
