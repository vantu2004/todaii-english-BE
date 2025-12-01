package com.todaii.english.server.article;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.server.event.EventService;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.enums.EventType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;
import com.todaii.english.shared.response.AIResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleParagraphService {
	private final ArticleParagraphRepository articleParagraphRepository;
	private final ArticleRepository articleRepository;
	private final ModelMapper modelMapper;
	private final GeminiPort geminiPort;
	private final EventService eventService;

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

	public String translateParagraph(Long currentAdminId, String textEn) {
		String prompt = String.format(Gemini.TRANSLATE_PROMPT, textEn);
		AIResponse aiResponse = geminiPort.generateText(prompt);

		eventService.logAdmin(currentAdminId, EventType.AI_REQUEST, 1,
				Map.of("input_token", aiResponse.getInputToken(), "output_token", aiResponse.getOutputToken()));

		return aiResponse.getText();
	}

	public void delete(Long id) {
		if (!articleParagraphRepository.existsById(id)) {
			throw new BusinessException(404, "Paragraph not found");
		}
		articleParagraphRepository.deleteById(id);
	}

}
