package com.todaii.english.server.article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.core.port.NewsApiPort;
import com.todaii.english.shared.response.NewsApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;
	private final NewsApiPort newsApiPort;

	@Transactional
	public NewsApiResponse fetchFromNewsApi(String query, int pageSize, int page, String sortBy) {
		return newsApiPort.fetchFromNewsApi(query, pageSize, page, sortBy);
	}
}
