package com.todaii.english.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryWord;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryWord, Long> {
  // Spring sẽ dịch thành `LIKE 'prefix%'`. ko dùng '%prefix%' vì B+ Tree tìm tiền tố từ trái sang
  // phải nên nếu để % ở đầu MySQL sẽ vứt Index đi và quét toàn bộ 350k dòng.
  Page<DictionaryWord> findByWordStartingWith(String prefix, Pageable pageable);

  // Nó chỉ check trên node lá của cây B+ Tree (Index) trên RAM, tốc độ phản hồi ~0.1ms.
  boolean existsByWord(String word);

  // Tìm các ID lớn hơn lastId, lấy Limit N dòng
  @Query("SELECT d FROM DictionaryWord d WHERE d.id > :lastId ORDER BY d.id ASC")
  List<DictionaryWord> findNextPageByCursor(Long lastId, Pageable pageable);

  Optional<DictionaryWord> findByWord(String word);

  List<DictionaryWord> findAllByWordIn(List<String> vocabs);
}
