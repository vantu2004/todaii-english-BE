package com.todaii.english.server.dictionary;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryEntry;

@Repository
public interface DictionaryEntryRepository extends JpaRepository<DictionaryEntry, Long> {
	/*
	 * Tìm kiếm theo headword (Spring Data tự hiểu), truyền vào 1 từ và search từ đó
	 * trong db
	 */
	public List<DictionaryEntry> findByHeadwordContainingIgnoreCase(String keyword);

	/*
	 * truyền vào 1 collection và xét các row trong db thỏa vs collection truyền vào
	 * ko
	 */
	@Query("SELECT e FROM DictionaryEntry e WHERE LOWER(e.headword) IN ?1")
	public List<DictionaryEntry> findByHeadwordInIgnoreCase(Collection<String> words);

	public boolean existsByHeadword(String headword);
}
