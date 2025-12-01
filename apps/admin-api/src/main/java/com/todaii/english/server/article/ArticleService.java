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
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.port.NewsApiPort;
import com.todaii.english.server.dictionary.DictionaryEntryRepository;
import com.todaii.english.server.event.EventService;
import com.todaii.english.server.topic.TopicRepository;
import com.todaii.english.shared.enums.EventType;
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
	private final EventService eventService;

	public NewsApiResponse fetchFromNewsApi(Long currentAdminId, String query, int pageSize, int page, String sortBy) {
		NewsApiResponse newsApiResponse = newsApiPort.fetchFromNewsApi(query, pageSize, page, sortBy);

		eventService.logAdmin(currentAdminId, EventType.NEWSAPI_REQUEST, 1, null);

		return newsApiResponse;
	}

	public Article create(ArticleRequest request) {
		Article article = modelMapper.map(request, Article.class);

		article.setTopics(new HashSet<>(topicRepository.findAllById(request.getTopicIds())));

		return articleRepository.save(article);

	}

	@Deprecated
	public List<Article> findAll() {
		return articleRepository.findAll();
	}

	public Page<Article> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
		return search(null, page, size, sortBy, direction, keyword);
	}

	public Page<Article> findByTopicId(Long topicId, int page, int size, String sortBy, String direction,
			String keyword) {
		if (!topicRepository.existsById(topicId)) {
			throw new BusinessException(404, "Topic not found");
		}

		return search(topicId, page, size, sortBy, direction, keyword);
	}

	private Page<Article> search(Long topicId, int page, int size, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return articleRepository.search(topicId, keyword, pageable);
	}

	public Article findById(Long id) {
		return articleRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Article not found"));
	}

	public Article update(Long id, ArticleRequest request) {
		Article article = findById(id);

		modelMapper.map(request, article);

		article.setTopics(new HashSet<>(topicRepository.findAllById(request.getTopicIds())));

		return articleRepository.save(article);
	}

	public void toggleEnabled(Long id) {
		Article article = findById(id);
		article.setEnabled(!article.getEnabled());

		articleRepository.save(article);
	}

	public void deleteById(Long id) {
		articleRepository.deleteById(id);
	}

	public Article addWordToArticle(Long articleId, Long wordId) {
		Article article = findById(articleId);

		DictionaryEntry dictionaryEntry = dictionaryEntryRepository.findById(wordId)
				.orElseThrow(() -> new BusinessException(404, "Word not found"));
		article.getWords().add(dictionaryEntry);

		return articleRepository.save(article);
	}

	public Article removeWordFromArticle(Long articleId, Long wordId) {
		Article article = findById(articleId);

		DictionaryEntry dictionaryEntry = dictionaryEntryRepository.findById(wordId)
				.orElseThrow(() -> new BusinessException(404, "Word not found"));

		boolean removed = article.getWords().remove(dictionaryEntry);
		if (!removed) {
			throw new BusinessException(400, "Word not found in article");
		}

		return articleRepository.save(article);
	}

	public Article removeAllWordsFromArticle(Long articleId) {
		Article article = findById(articleId);
		article.getWords().clear();

		return articleRepository.save(article);
	}

}
