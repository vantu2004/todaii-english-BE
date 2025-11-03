package com.todaii.english.server.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.ArticleParagraph;

@Repository
public interface ArticleParagraphRepository extends JpaRepository<ArticleParagraph, Long> {

}
