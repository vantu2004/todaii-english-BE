package com.todaii.english.server.article;

import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.port.NewsApiPort;
import com.todaii.english.server.dictionary.DictionaryEntryRepository;
import com.todaii.english.server.topic.TopicRepository;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleRequest;
import com.todaii.english.shared.response.NewsApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;
	private final NewsApiPort newsApiPort;
	private final ModelMapper modelMapper;
	private final TopicRepository topicRepository;
	private final DictionaryEntryRepository dictionaryEntryRepository;

	public NewsApiResponse fetchFromNewsApi(String query, int pageSize, int page, String sortBy) {
		return newsApiPort.fetchFromNewsApi(query, pageSize, page, sortBy);
	}

	public Article create(ArticleRequest request) {
		Article article = modelMapper.map(request, Article.class);

		// Liên kết topic nếu có
		if (request.getTopicIds() != null && !request.getTopicIds().isEmpty()) {
			article.setTopics(new HashSet<>(topicRepository.findAllById(request.getTopicIds())));
		}

		return articleRepository.save(article);

	}

	@Deprecated
	public List<Article> findAll() {
		return articleRepository.findAll();
	}

	public Page<Article> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return articleRepository.search(keyword, pageable);
	}

	public Article findById(Long id) {
		return articleRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Article not found"));
	}

	public Article update(Long id, ArticleRequest request) {
		Article article = findById(id);

		modelMapper.map(request, article);

		// Liên kết lại topic nếu có
		if (request.getTopicIds() != null && !request.getTopicIds().isEmpty()) {
			article.setTopics(new HashSet<>(topicRepository.findAllById(request.getTopicIds())));
		}

		// Liên kết lại dictionary entries nếu có
		if (request.getDictionaryEntryIds() != null && !request.getDictionaryEntryIds().isEmpty()) {
			article.setEntries(new HashSet<>(dictionaryEntryRepository.findAllById(request.getDictionaryEntryIds())));
		}

		return articleRepository.save(article);
	}

	public void toggleEnabled(Long id) {
		Article article = findById(id);
		if (article.getParagraphs().isEmpty()) {
			throw new BusinessException(400, "Cannot enable the article because it has no content");
		}

		article.setEnabled(!article.getEnabled());

		articleRepository.save(article);
	}

	public void deleteById(Long id) {
		articleRepository.deleteById(id);
	}
}
