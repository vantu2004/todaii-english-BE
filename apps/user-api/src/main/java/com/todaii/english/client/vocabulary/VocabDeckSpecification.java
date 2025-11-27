package com.todaii.english.client.vocabulary;

import org.springframework.data.jpa.domain.Specification;

import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.core.entity.VocabDeck_;
import com.todaii.english.core.entity.VocabGroup;
import com.todaii.english.core.entity.VocabGroup_;
import com.todaii.english.shared.enums.CefrLevel;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class VocabDeckSpecification {
	public static Specification<VocabDeck> hasKeyword(String keyword) {
		return (root, query, cb) -> {
			if (keyword == null || keyword.trim().isEmpty())
				return null;

			String likeValue = "%" + keyword.toLowerCase() + "%";

			return cb.or(cb.like(cb.lower(root.get(VocabDeck_.NAME)), likeValue),
					cb.like(root.get(VocabDeck_.DESCRIPTION), likeValue));
		};
	}

	public static Specification<VocabDeck> hasCefrLevel(CefrLevel cefrLevel) {
		return (root, query, cb) -> cefrLevel == null ? null : cb.equal(root.get(VocabDeck_.cefrLevel), cefrLevel);
	}

	public static Specification<VocabDeck> hasGroup(String alias) {
		return (root, query, cb) -> {
			if (alias == null || alias.isBlank())
				return null;
			query.distinct(true);
			Join<VocabDeck, VocabGroup> groupJoin = root.join(VocabDeck_.GROUPS, JoinType.INNER);

			return cb.equal(groupJoin.get(VocabGroup_.ALIAS), alias);
		};
	}

	public static Specification<VocabDeck> isEnabled() {
		return (root, query, cb) -> cb.isTrue(root.get(VocabDeck_.ENABLED));
	}
}
