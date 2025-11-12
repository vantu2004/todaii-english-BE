package com.todaii.english.server.article;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	// nếu topicId != null nghĩa là tìm các article dựa theo topicId, dùng chung cho
	// 2 hàm getArticles
	@Query("""
			SELECT DISTINCT a FROM Article a
			LEFT JOIN a.topics t
			WHERE
				(?1 IS NULL OR t.id = ?1) AND
				(
					?2 IS NULL
				    OR STR(a.id) LIKE CONCAT('%', ?2, '%')
				    OR LOWER(a.sourceId) LIKE LOWER(CONCAT('%', ?2, '%'))
				    OR LOWER(a.sourceName) LIKE LOWER(CONCAT('%', ?2, '%'))
				    OR LOWER(a.author) LIKE LOWER(CONCAT('%', ?2, '%'))
				    OR LOWER(a.title) LIKE LOWER(CONCAT('%', ?2, '%'))
				    OR a.description LIKE CONCAT('%', ?2, '%')
				    OR LOWER(a.cefrLevel) LIKE LOWER(CONCAT('%', ?2, '%'))
				)
			""")
	public Page<Article> search(Long topicId, String keyword, Pageable pageable);

	public List<Article> findAllByEntries_Id(Long entryId);
}
