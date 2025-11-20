package com.todaii.english.client.article;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.User;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;
	private final UserRepository userRepository;

	public List<Article> getLatestArticles(int size) {
		return articleRepository.findAllByEnabledTrueOrderByCreatedAtDesc(PageRequest.of(0, size)).getContent();
	}

	public List<Article> getTopArticles(int size) {
		return articleRepository.findAllByEnabledTrueOrderByViewsDesc(PageRequest.of(0, size)).getContent();
	}

	public Page<Article> getArticlesByDate(LocalDate date, String keyword, int page, int size, String sortBy,
			String direction) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(23, 59, 59);

		return articleRepository.findByUpdatedDateRangeAndKeyword(startOfDay, endOfDay, keyword, pageable);
	}

	public Article findById(Long id) {
		Article article = articleRepository.findById(id)
				.orElseThrow(() -> new BusinessException(404, "Article not found"));

		// mỗi truy cập tính 1 lượt view
		article.setViews(article.getViews() + 1);

		return articleRepository.save(article);
	}

	public Page<Article> search(String keyword, int page, int size, String sortBy, String direction) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return articleRepository.search(keyword, pageable);
	}

	public Page<DictionaryEntry> getPagedVocabulary(Long id, int page, int size) {
		if (!articleRepository.existsById(id)) {
			throw new BusinessException(404, "Article not found");
		}

		Pageable pageable = PageRequest.of(page - 1, size);

		return articleRepository.findPagedWordsByArticleId(id, pageable);
	}

	public List<Article> getRelatedArticles(Long articleId, int limit) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessException(404, "Article not found"));

		List<Long> topicIds = article.getTopics().stream().map(topic -> topic.getId()).toList();

		// Lấy tất cả bài cùng topic
		List<Article> related = articleRepository.findRelatedByTopics(articleId, topicIds);
		if (related.isEmpty()) {
			return getFallbackArticles(articleId, article.getCefrLevel(), limit);
		}

		// Nếu chưa đủ thì bổ sung article cùng CEFR
		if (related.size() < limit) {
			int remaining = limit - related.size();
			List<Article> fallback = articleRepository.findFallbackByCefr(articleId, article.getCefrLevel(),
					PageRequest.of(0, remaining));

			// Gộp & loại trùng theo ID
			Set<Long> existingIds = related.stream().map(Article::getId).collect(Collectors.toSet());
			for (Article a : fallback) {
				if (!existingIds.contains(a.getId())) {
					related.add(a);
					existingIds.add(a.getId());
				}
				if (related.size() >= limit)
					break;
			}
		}

		// Random hóa thứ tự kết quả
		Collections.shuffle(related);

		// Giới hạn kết quả cuối cùng
		if (related.size() > limit) {
			related = related.subList(0, limit);
		}

		return related;
	}

	private List<Article> getFallbackArticles(Long articleId, CefrLevel cefrLevel, int limit) {
		Pageable pageable = PageRequest.of(0, limit);

		return articleRepository.findFallbackByCefr(articleId, cefrLevel, pageable);
	}

	public Page<Article> filterArticles(String keyword, String sourceName, CefrLevel cefrLevel, Integer minViews,
			Long topicId, int page, int size, String sortBy, String direction) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		Specification<Article> spec = Specification.where(ArticleSpecification.isEnabled())
				.and(ArticleSpecification.hasKeyword(keyword)).and(ArticleSpecification.hasSourceName(sourceName))
				.and(ArticleSpecification.hasCefrLevel(cefrLevel)).and(ArticleSpecification.hasMinViews(minViews))
				.and(ArticleSpecification.hasTopic(topicId));

		return articleRepository.findAll(spec, pageable);
	}

	public List<Article> getSavedArticlesByUserId(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(404, "User not found"));
		Set<Article> savedSet = user.getSavedArticles();

		return new ArrayList<>(savedSet);
	}

	public Boolean isSavedByUser(Long articleId, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(404, "User not found"));

		return user.getSavedArticles().stream().anyMatch(a -> a.getId().equals(articleId));
	}

}
