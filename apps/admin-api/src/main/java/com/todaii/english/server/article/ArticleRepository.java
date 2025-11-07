package com.todaii.english.server.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	@Query("""
			SELECT a FROM Article a
			WHERE
			    ?1 IS NULL
			    OR STR(a.id) LIKE CONCAT('%', ?1, '%')
			    OR LOWER(a.sourceId) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(a.sourceName) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(a.author) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(a.title) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR a.description LIKE CONCAT('%', ?1, '%')
			    OR LOWER(a.cefrLevel) LIKE LOWER(CONCAT('%', ?1, '%'))
			""")
	public Page<Article> search(String keyword, Pageable pageable);
}
