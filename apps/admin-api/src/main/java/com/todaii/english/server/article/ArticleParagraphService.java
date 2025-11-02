package com.todaii.english.server.article;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleParagraphService {
	private final ArticleParagraphRepository articleParagraphRepository;
	private final ArticleRepository articleRepository;
	private final ModelMapper modelMapper;

	public List<ArticleParagraph> getByArticleId(Long articleId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessException(404, "Article not found"));
		return article.getParagraphs().stream().sorted((a, b) -> a.getParaOrder().compareTo(b.getParaOrder())).toList();
	}

	public ArticleParagraph create(Long articleId, ArticleParagraphRequest request) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessException(404, "Article not found"));

		ArticleParagraph paragraph = modelMapper.map(request, ArticleParagraph.class);
		paragraph.setArticle(article);

		return articleParagraphRepository.save(paragraph);
	}

	public ArticleParagraph update(Long id, ArticleParagraphRequest request) {
		ArticleParagraph paragraph = articleParagraphRepository.findById(id)
				.orElseThrow(() -> new BusinessException(404, "Paragraph not found"));

		modelMapper.map(request, paragraph);

		return articleParagraphRepository.save(paragraph);
	}

	public void delete(Long id) {
		if (!articleParagraphRepository.existsById(id)) {
			throw new BusinessException(404, "Paragraph not found");
		}
		articleParagraphRepository.deleteById(id);
	}
}
