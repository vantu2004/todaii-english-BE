package com.todaii.english.server.dictionary;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryEntry;

@Repository
public interface DictionaryEntryRepository extends JpaRepository<DictionaryEntry, Long> {
	@Query("""
			    SELECT DISTINCT e FROM DictionaryEntry e
			    WHERE
			        ?1 IS NULL
			        OR STR(e.id) LIKE CONCAT('%', ?1, '%')
			        OR LOWER(e.headword) LIKE LOWER(CONCAT('%', ?1, '%'))
			        OR LOWER(e.ipa) LIKE LOWER(CONCAT('%', ?1, '%'))
			""")
	public Page<DictionaryEntry> search(String keyword, Pageable pageable);

	/*
	 * truyền vào 1 collection và xét các row trong db thỏa vs collection truyền vào
	 * ko
	 */
	@Query("SELECT e FROM DictionaryEntry e WHERE LOWER(e.headword) IN ?1")
	public List<DictionaryEntry> findByHeadwordInIgnoreCase(Collection<String> words);

	public boolean existsByHeadword(String headword);
}
