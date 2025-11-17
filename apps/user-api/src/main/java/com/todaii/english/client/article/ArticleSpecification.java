package com.todaii.english.client.article;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.Article_;
import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.CefrLevel;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecification {
	
	public static Specification<Article> hasKeyword(String keyword) {
		return (root, query, cb) -> keyword == null ? null
				: cb.like(cb.lower(root.get(Article_.TITLE)), "%" + keyword.toLowerCase() + "%");
	}

	public static Specification<Article> hasSourceName(String sourceName) {
		return (root, query, cb) -> sourceName == null ? null
				: cb.like(cb.lower(root.get(Article_.SOURCE_NAME)), "%" + sourceName.toLowerCase() + "%");
	}

	public static Specification<Article> hasCefrLevel(CefrLevel level) {
		return (root, query, cb) -> level == null ? null : cb.equal(root.get(Article_.CEFR_LEVEL), level);
	}

	public static Specification<Article> hasMinViews(Integer minViews) {
		return (root, query, cb) -> minViews == null ? null
				: cb.greaterThanOrEqualTo(root.get(Article_.VIEWS), minViews);
	}

	public static Specification<Article> hasTopic(Long topicId) {
		return (root, query, cb) -> {
			if (topicId == null)
				return null;
			Join<Article, Topic> topicJoin = root.join(Article_.TOPICS, JoinType.INNER);
			return cb.equal(topicJoin.get("id"), topicId);
		};
	}

	public static Specification<Article> isEnabled() {
		return (root, query, cb) -> cb.isTrue(root.get(Article_.ENABLED));
	}
}
