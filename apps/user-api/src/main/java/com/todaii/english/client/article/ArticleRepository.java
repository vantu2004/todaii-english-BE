package com.todaii.english.client.article;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Article;

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

}
