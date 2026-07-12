package com.todaii.english.client.vocabulary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.vocabulary.VocabDeck;

@Repository
public interface VocabDeckRepository
    extends JpaRepository<VocabDeck, Long>, JpaSpecificationExecutor<VocabDeck> {
  @Query("SELECT v FROM VocabDeck v WHERE v.enabled = true AND v.id = ?1")
  public Optional<VocabDeck> findById(Long id);

  // Recommend: Lấy ngẫu nhiên N deck theo CEFR level
  @Query(
      value =
          "SELECT * FROM vocab_decks d WHERE d.enabled = true AND d.cefr_level = :cefrLevel"
              + " ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<VocabDeck> findRandomByCefrLevel(
      @Param("cefrLevel") String cefrLevel, @Param("limit") int limit);

  // Recommend fallback: Lấy ngẫu nhiên N deck bất kỳ
  @Query(
      value = "SELECT * FROM vocab_decks d WHERE d.enabled = true ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<VocabDeck> findRandomDecks(@Param("limit") int limit);
}
