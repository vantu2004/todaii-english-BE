package com.todaii.english.client.article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.shared.enums.CefrLevel;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	// lấy 10 bài báo đã enabled gần nhất sort theo createdAt chiều giảm dần
	public Page<Article> findAllByEnabledTrueOrderByCreatedAtDesc(Pageable pageable);

	@Query("""
			    SELECT a FROM Article a
			    WHERE a.enabled = true
			      AND a.updatedAt BETWEEN ?1 AND ?2
			      AND (
			           ?3 IS NULL
					   OR LOWER(a.sourceId) LIKE LOWER(CONCAT('%', ?3, '%'))
					   OR LOWER(a.sourceName) LIKE LOWER(CONCAT('%', ?3, '%'))
					   OR LOWER(a.author) LIKE LOWER(CONCAT('%', ?3, '%'))
					   OR LOWER(a.title) LIKE LOWER(CONCAT('%', ?3, '%'))
					   OR a.description LIKE CONCAT('%', ?3, '%')
					   OR LOWER(a.cefrLevel) LIKE LOWER(CONCAT('%', ?3, '%'))
			      )
			""")
	public Page<Article> findByUpdatedDateRangeAndKeyword(LocalDateTime startOfDay, LocalDateTime endOfDay,
			String keyword, Pageable pageable);

	@Query("SELECT a FROM Article a WHERE a.enabled = true AND a.id = ?1")
	public Optional<Article> findById(Long id);

	@Query("""
			    SELECT a FROM Article a
			    WHERE a.enabled = true
			      AND (
			           ?1 IS NULL
					   OR LOWER(a.sourceId) LIKE LOWER(CONCAT('%', ?1, '%'))
					   OR LOWER(a.sourceName) LIKE LOWER(CONCAT('%', ?1, '%'))
					   OR LOWER(a.author) LIKE LOWER(CONCAT('%', ?1, '%'))
					   OR LOWER(a.title) LIKE LOWER(CONCAT('%', ?1, '%'))
					   OR a.description LIKE CONCAT('%', ?1, '%')
					   OR LOWER(a.cefrLevel) LIKE LOWER(CONCAT('%', ?1, '%'))
			      )
			""")
	public Page<Article> search(String keyword, Pageable pageable);

	@Query("""
			    SELECT d FROM Article a
			    JOIN a.entries d
			    WHERE a.id = ?1
			    ORDER BY d.headword ASC
			""")
	public Page<DictionaryEntry> findPagedEntriesByArticleId(Long articleId, Pageable pageable);

	@Query("""
			    SELECT DISTINCT a
			    FROM Article a
			    JOIN a.topics t
			    WHERE a.enabled = true
			      AND a.id <> ?1
			      AND t.id IN ?2
			""")
	public List<Article> findRelatedByTopics(Long articleId, List<Long> topicIds);

	@Query("""
			    SELECT a
			    FROM Article a
			    WHERE a.enabled = true
			      AND a.id <> ?1
			      AND a.cefrLevel = ?2
			""")
	public List<Article> findFallbackByCefr(Long articleId, CefrLevel cefrLevel, Pageable pageable);

}
