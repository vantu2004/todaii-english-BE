package com.todaii.english.server.article;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleParagraphService {
	private final ArticleParagraphRepository articleParagraphRepository;
	private final ArticleRepository articleRepository;
	private final ModelMapper modelMapper;
	private final GeminiPort geminiPort;

	public List<ArticleParagraph> getByArticleId(Long articleId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessException(404, "Article not found"));
		return article.getParagraphs().stream().sorted((a, b) -> a.getParaOrder().compareTo(b.getParaOrder())).toList();
	}

	public ArticleParagraph save(Long articleId, ArticleParagraphRequest request) {
		ArticleParagraph paragraph;

		// update
		if (request.getId() != null) {
			paragraph = articleParagraphRepository.findById(request.getId())
					.orElseThrow(() -> new BusinessException(404, "Paragraph not found"));

			// kiểm tra paragraph thuộc articleId
			if (!paragraph.getArticle().getId().equals(articleId)) {
				throw new BusinessException(400, "Paragraph does not belong to the given article");
			}
		}
		// create
		else {
			Article article = articleRepository.findById(articleId)
					.orElseThrow(() -> new BusinessException(404, "Article not found"));

			paragraph = new ArticleParagraph();
			paragraph.setArticle(article);
		}

		modelMapper.map(request, paragraph);
		return articleParagraphRepository.save(paragraph);
	}

	public String translateParagraph(String textEn) {
		String prompt = String.format(Gemini.TRANSLATE_PROMPT, textEn);

		return geminiPort.generateText(prompt).trim();
	}

	public void delete(Long id) {
		if (!articleParagraphRepository.existsById(id)) {
			throw new BusinessException(404, "Paragraph not found");
		}
		articleParagraphRepository.deleteById(id);
	}

}
