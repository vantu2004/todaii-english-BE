package com.todaii.english.server.dictionary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryEntry;

@Repository
public interface DictionaryEntryReposiroty extends JpaRepository<DictionaryEntry, Long> {
	public List<DictionaryEntry> findByHeadwordContainingIgnoreCase(String keyword);
}
