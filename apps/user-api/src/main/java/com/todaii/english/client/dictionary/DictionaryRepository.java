package com.todaii.english.client.dictionary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryEntry;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryEntry, Long> {
	public List<DictionaryEntry> findAllByHeadword(String word);

}
