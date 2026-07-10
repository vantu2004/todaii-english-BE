package com.todaii.english.server.question;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Question;
import com.todaii.english.core.entity.article.ArticleParagraph;
import com.todaii.english.core.entity.video.VideoLyricLine;
import com.todaii.english.infra.service.AiFallbackService;
import com.todaii.english.server.article.ArticleParagraphRepository;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.video.VideoLyricLineRepository;
import com.todaii.english.server.video.VideoRepository;
import com.todaii.english.shared.dto.QuestionDTO;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.TopicType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.QuestionRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {
  private final QuestionRepository questionRepository;
  private final ArticleRepository articleRepository;
  private final ArticleParagraphRepository articleParagraphRepository;
  private final VideoRepository videoRepository;
  private final VideoLyricLineRepository videoLyricLineRepository;
  private final ModelMapper modelMapper;
  private final AiFallbackService aiFallbackService;
  private final ObjectMapper objectMapper;

  @Value("classpath:/promptTemplates/question/systemPromptGenerateQuestionTemplate.st")
  private Resource systemPromptTemplate;

  @Value("classpath:/promptTemplates/question/userPromptGenerateQuestionTemplate.st")
  private Resource userPromptTemplate;

  public List<Question> getAllQuestions(TopicType topicType, Long targetId) {
    return questionRepository.findByTopicTypeAndContentId(topicType, targetId);
  }

  public Question createQuestion(QuestionRequest request) {
    if (request.getTopicType() == null || request.getContentId() == null) {
      throw new BusinessException(400, "TopicType and ContentId are required for creation");
    }
    // Verify existence of content
    if (request.getTopicType() == TopicType.ARTICLE) {
      if (!articleRepository.existsById(request.getContentId())) {
        throw new BusinessException(404, "Article not found");
      }
    } else if (request.getTopicType() == TopicType.VIDEO) {
      if (!videoRepository.existsById(request.getContentId())) {
        throw new BusinessException(404, "Video not found");
      }
    }

    Question question = modelMapper.map(request, Question.class);

    return questionRepository.save(question);
  }

  public Question updateQuestion(Long id, QuestionRequest request) {
    Question existing =
        questionRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(404, "Question not found"));

    existing.setQuestionText(request.getQuestionText());
    existing.setOptionA(request.getOptionA());
    existing.setOptionB(request.getOptionB());
    existing.setOptionC(request.getOptionC());
    existing.setOptionD(request.getOptionD());
    existing.setCorrectOption(request.getCorrectOption());
    existing.setExplanation(request.getExplanation());

    return questionRepository.save(existing);
  }

  public void deleteQuestion(Long id) {
    if (!questionRepository.existsById(id)) {
      throw new BusinessException(404, "Question not found");
    }

    questionRepository.deleteById(id);
  }

  public List<Question> generateQuestions(
      TopicType topicType, Long targetId, int count, Long currentAdminId) {
    String textEn = "";

    if (topicType == TopicType.ARTICLE) {
      if (!articleRepository.existsById(targetId)) {
        throw new BusinessException(404, "Article not found");
      }

      List<ArticleParagraph> paragraphs = articleParagraphRepository.findByArticleId(targetId);
      if (paragraphs.isEmpty()) {
        throw new BusinessException(400, "Không có nội dung để sinh câu hỏi");
      }

      textEn =
          paragraphs.stream()
              .map(ArticleParagraph::getTextEn)
              .filter(Objects::nonNull)
              .collect(Collectors.joining("\n"));
    } else if (topicType == TopicType.VIDEO) {
      if (!videoRepository.existsById(targetId)) {
        throw new BusinessException(404, "Video not found");
      }

      List<VideoLyricLine> lyricLines = videoLyricLineRepository.findAllByVideoId(targetId);
      if (lyricLines.isEmpty()) {
        throw new BusinessException(400, "Không có nội dung để sinh câu hỏi");
      }

      textEn =
          lyricLines.stream()
              .map(VideoLyricLine::getTextEn)
              .filter(Objects::nonNull)
              .collect(Collectors.joining("\n"));
    }

    if (textEn.trim().isEmpty()) {
      throw new BusinessException(400, "Không có nội dung để sinh câu hỏi");
    }

    List<QuestionDTO> questionDTOS = getGenerateQuestionResponse(count, currentAdminId, textEn);
    List<Question> questions =
        questionDTOS.stream()
            .map(
                questionDTO -> {
                  Question question = modelMapper.map(questionDTO, Question.class);
                  question.setTopicType(topicType);
                  question.setContentId(targetId);

                  return question;
                })
            .collect(Collectors.toList());

    return questionRepository.saveAll(questions);
  }

  private List<QuestionDTO> getGenerateQuestionResponse(
      int count, Long currentAdminId, String textEn) {
    try {
      final String finalSourceText = textEn;
      ChatResponse response =
          aiFallbackService.executeWithFallback(
              currentAdminId,
              ActorType.ADMIN,
              client ->
                  client
                      .prompt()
                      .system(sys -> sys.text(systemPromptTemplate).param("count", count))
                      .user(
                          usr -> usr.text(userPromptTemplate).param("input_text", finalSourceText))
                      .call()
                      .chatResponse());

      String jsonContent = response.getResult().getOutput().getText();
      log.info("AI response for question generation: {}", jsonContent);

      return objectMapper.readValue(jsonContent, new TypeReference<List<QuestionDTO>>() {});
    } catch (Exception ex) {
      log.error("💥 Failed to generate questions using AI", ex);
      throw new BusinessException(500, "Failed to generate questions: " + ex.getMessage());
    }
  }
}
