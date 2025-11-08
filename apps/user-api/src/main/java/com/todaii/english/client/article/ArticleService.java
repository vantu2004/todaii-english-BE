package com.todaii.english.client.article;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Article;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;

	public List<Article> getLatestArticles() {
		return articleRepository.findAllByEnabledTrueOrderByCreatedAtDesc(PageRequest.of(0, 10)).getContent();
	}

	public Page<Article> getArticlesByDate(LocalDate date, String keyword, int page, int size, String sortBy,
			String direction) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(23, 59, 59);

		return articleRepository.findByUpdatedDateRangeAndKeyword(startOfDay, endOfDay, keyword, pageable);
	}

}
