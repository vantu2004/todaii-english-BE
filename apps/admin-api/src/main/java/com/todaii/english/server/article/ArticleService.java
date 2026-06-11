package com.todaii.english.server.article;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.entity.article.Article;
import com.todaii.english.core.entity.article.ArticleParagraph;
import com.todaii.english.core.port.*;
import com.todaii.english.core.repository.DictionaryRepository;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.server.topic.TopicRepository;
import com.todaii.english.shared.enums.ActorType;
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
  private final DictionaryRepository dictionaryRepository;
  private final UsageStatisticPort usageStatisticPort;
  private final VocabExtractionPort vocabExtractionPort;
  private final TtsPort ttsPort;
  private final CloudinaryPort cloudinaryPort;

  public NewsApiResponse fetchFromNewsApi(
      Long currentAdminId, String query, int pageSize, int page, String sortBy) {
    NewsApiResponse newsApiResponse = newsApiPort.fetchFromNewsApi(query, pageSize, page, sortBy);

    UsageStatistic newsApiStatistic = usageStatisticPort.createNewsApiStatistic(currentAdminId);
    usageStatisticPort.createUsageStatistic(newsApiStatistic);

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

  public Page<Article> findAllPaged(
      int page, int size, String sortBy, String direction, String keyword) {
    return search(null, page, size, sortBy, direction, keyword);
  }

  public Page<Article> findByTopicId(
      Long topicId, int page, int size, String sortBy, String direction, String keyword) {
    if (!topicRepository.existsById(topicId)) {
      throw new BusinessException(404, "Topic not found");
    }

    return search(topicId, page, size, sortBy, direction, keyword);
  }

  private Page<Article> search(
      Long topicId, int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    return articleRepository.search(topicId, AdminUtils.formatSearchKeyword(keyword), pageable);
  }

  public Article findById(Long id) {
    return articleRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Article not found"));
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

  public List<DictionaryWord> vocabExtraction(Long currentAdminId, Long articleId) {
    Article article = findById(articleId);
    if (article.getParagraphs().isEmpty()) {
      throw new BusinessException(400, "This article has no content.");
    }

    String textEn =
        article.getParagraphs().stream()
            .map(ArticleParagraph::getTextEn)
            .collect(Collectors.joining("\n"));

    String words =
        article.getWords().stream().map(DictionaryWord::getWord).collect(Collectors.joining("\n"));

    List<String> vocabs =
        vocabExtractionPort.vocabExtraction(textEn, words, currentAdminId, ActorType.ADMIN);

    return dictionaryRepository.findAllByWordIn(vocabs);
  }

  public Article addWordToArticle(Long articleId, Long wordId) {
    Article article = findById(articleId);

    DictionaryWord dictionaryWord =
        dictionaryRepository
            .findById(wordId)
            .orElseThrow(() -> new BusinessException(404, "Word not found"));
    article.getWords().add(dictionaryWord);

    return articleRepository.save(article);
  }

  public Article removeWordFromArticle(Long articleId, Long wordId) {
    Article article = findById(articleId);

    DictionaryWord dictionaryWord =
        dictionaryRepository
            .findById(wordId)
            .orElseThrow(() -> new BusinessException(404, "Word not found"));

    boolean removed = article.getWords().remove(dictionaryWord);
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

  public Article uploadTtsFile(Long currentAdminId, Long articleId) throws IOException {
    Article article = findById(articleId);
    Set<ArticleParagraph> paragraphs = article.getParagraphs();
    if (paragraphs == null || paragraphs.isEmpty()) {
      throw new BusinessException(400, "Article has no paragraphs");
    }

    String text =
        article.getParagraphs().stream()
            .map(ArticleParagraph::getTextEn)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"));

    byte[] ttsBytes = ttsPort.call(text);

    String ttsUrl = cloudinaryPort.uploadTtsFile(ttsBytes, "article");

    article.setAudioUrl(ttsUrl);
    Article savedArticle = articleRepository.save(article);

    usageStatisticPort.createUsageStatistic(
        usageStatisticPort.createCloudinaryStatistic(currentAdminId, ActorType.ADMIN));

    return savedArticle;
  }

  public Article deleteTtsFile(Long articleId, String fileUrl) {
    Article article = findById(articleId);
    article.setAudioUrl(null);

    Article savedArticle = articleRepository.save(article);

    cloudinaryPort.deleteFile(fileUrl);

    return savedArticle;
  }
}
