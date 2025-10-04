package com.todaii.english.server.dictionary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryEntry;

@Repository
public interface DictionaryEntryRepository extends JpaRepository<DictionaryEntry, Long> {

	// Tìm kiếm theo headword (Spring Data tự hiểu)
	List<DictionaryEntry> findByHeadwordContainingIgnoreCase(String keyword);

	// Các hàm cơ bản đã có sẵn trong JpaRepository:
	// findAll(), findById(), save(), deleteById(), existsById() ...

	boolean existsByHeadword(String headword);
}
