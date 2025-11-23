package com.todaii.english.client.video;

import com.todaii.english.core.entity.Video;
import com.todaii.english.core.entity.Video_;
import com.todaii.english.core.entity.Topic;
import com.todaii.english.core.entity.Topic_;
import com.todaii.english.shared.enums.CefrLevel;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class VideoSpecification {

	public static Specification<Video> hasKeyword(String keyword) {
		return (root, query, cb) -> {
			if (keyword == null || keyword.trim().isEmpty())
				return null;

			String likeValue = "%" + keyword.toLowerCase() + "%";

			return cb.or(cb.like(cb.lower(root.get(Video_.title)), likeValue),
					cb.like(cb.lower(root.get(Video_.authorName)), likeValue),
					cb.like(cb.lower(root.get(Video_.providerName)), likeValue));
		};
	}

	public static Specification<Video> hasCefrLevel(CefrLevel cefrLevel) {
		return (root, query, cb) -> cefrLevel == null ? null : cb.equal(root.get(Video_.cefrLevel), cefrLevel);
	}

	public static Specification<Video> hasMinViews(Integer minViews) {
		return (root, query, cb) -> minViews == null ? null : cb.greaterThanOrEqualTo(root.get(Video_.VIEWS), minViews);
	}

	public static Specification<Video> hasTopic(String alias) {
		return (root, query, cb) -> {
			if (alias == null || alias.isBlank())
				return null;
			query.distinct(true);
			Join<Video, Topic> topicJoin = root.join(Video_.topics, JoinType.INNER);
			return cb.equal(topicJoin.get(Topic_.alias), alias);
		};
	}

	public static Specification<Video> isEnabled() {
		return (root, query, cb) -> cb.isTrue(root.get(Video_.enabled));
	}
}
