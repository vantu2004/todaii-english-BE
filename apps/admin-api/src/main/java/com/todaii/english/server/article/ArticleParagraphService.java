package com.todaii.english.server.article;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.article.Article;
import com.todaii.english.core.entity.article.ArticleParagraph;
import com.todaii.english.infra.service.AiFallbackService;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleParagraphService {
  private final ArticleParagraphRepository articleParagraphRepository;
  private final ArticleRepository articleRepository;
  private final ModelMapper modelMapper;
  private final AiFallbackService aiFallbackService;

  @Value("classpath:/promptTemplates/article/userPromptTranslateParaTemplate.st")
  private Resource userPromptTranslateParaTemplate;

  public List<ArticleParagraph> getByArticleId(Long articleId) {
    Article article =
        articleRepository
            .findById(articleId)
            .orElseThrow(() -> new BusinessException(404, "Article not found"));
    return article.getParagraphs().stream()
        .sorted(Comparator.comparing(ArticleParagraph::getParaOrder))
        .toList();
  }

  public ArticleParagraph save(Long articleId, ArticleParagraphRequest request) {
    ArticleParagraph paragraph;

    // update
    if (request.getId() != null) {
      paragraph =
          articleParagraphRepository
              .findById(request.getId())
              .orElseThrow(() -> new BusinessException(404, "Paragraph not found"));

      // kiểm tra paragraph thuộc articleId
      if (!paragraph.getArticle().getId().equals(articleId)) {
        throw new BusinessException(400, "Paragraph does not belong to the given article");
      }
    }
    // create
    else {
      Article article =
          articleRepository
              .findById(articleId)
              .orElseThrow(() -> new BusinessException(404, "Article not found"));

      paragraph = new ArticleParagraph();
      paragraph.setArticle(article);
    }

    modelMapper.map(request, paragraph);
    ArticleParagraph saved = articleParagraphRepository.save(paragraph);

    recalculateArticleEstimatedReadingTime(articleId);

    return saved;
  }

  public String translateParagraph(Long currentAdminId, String textEn) {
    // Truyền logic prompt dưới dạng Lambda Function để bên kia tự gán Chatclient
    // cho chạy
    ChatResponse response =
        aiFallbackService.executeWithFallback(
            currentAdminId,
            ActorType.ADMIN,
            client ->
                client
                    .prompt()
                    .user(
                        req ->
                            req.text(userPromptTranslateParaTemplate).param("input_text", textEn))
                    .call()
                    .chatResponse());

    return response.getResult().getOutput().getText();
  }

  public void delete(Long id) {
    ArticleParagraph paragraph =
        articleParagraphRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(404, "Paragraph not found"));
    Long articleId = paragraph.getArticle().getId();
    articleParagraphRepository.delete(paragraph);

    recalculateArticleEstimatedReadingTime(articleId);
  }

  public void recalculateArticleEstimatedReadingTime(Long articleId) {
    Article article =
        articleRepository
            .findById(articleId)
            .orElseThrow(() -> new BusinessException(404, "Article not found"));

    List<ArticleParagraph> paragraphs = articleParagraphRepository.findByArticleId(articleId);
    if (paragraphs.isEmpty()) {
      article.setEstimatedReadingTime(0);
    } else {
      String textEn =
          paragraphs.stream()
              .map(ArticleParagraph::getTextEn)
              .filter(Objects::nonNull)
              .collect(Collectors.joining("\n"));

      String trimmed = textEn.trim();
      int wordCount = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

      // mặc định wpm là 150, tuy nhiên đơn vị là phút nn phải x60 để convert sang giây
      int estimatedTime = (int) Math.ceil((double) wordCount * 60 / 150);

      article.setEstimatedReadingTime(estimatedTime);
    }

    articleRepository.save(article);
  }
}
