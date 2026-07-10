package com.todaii.english.server.article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.article.ArticleParagraph;

@Repository
public interface ArticleParagraphRepository extends JpaRepository<ArticleParagraph, Long> {
  List<ArticleParagraph> findByArticleId(Long articleId);
}
