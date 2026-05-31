package com.todaii.english.client.toeic_user_highlight;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.toeic_passage.PassageRepository;
import com.todaii.english.client.toeic_question.QuestionRepository;
import com.todaii.english.client.toeic_test_session.ToeicTestSessionRepository;
import com.todaii.english.core.entity.toeic.ToeicPassage;
import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.core.entity.toeic.ToeicUserHighlight;
import com.todaii.english.shared.dto.ToeicUserHighlightDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.client.ToeicHighlightRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToeicUserHighlightService {
  private final ToeicUserHighlightRepository highlightRepository;
  private final ToeicTestSessionRepository sessionRepository;
  private final QuestionRepository questionRepository;
  private final PassageRepository passageRepository;

  @Transactional
  public ToeicUserHighlightDTO createHighlight(Long userId, ToeicHighlightRequest request) {
    ToeicTestSession session =
        sessionRepository
            .findByIdAndUserId(request.getSessionId(), userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Test session not found or does not belong to the user"));

    ToeicQuestion question = null;
    if (request.getQuestionId() != null) {
      question =
          questionRepository
              .findById(request.getQuestionId())
              .orElseThrow(() -> new BusinessException(404, "Question not found"));

      if (question.getTest() == null
          || !question.getTest().getId().equals(session.getTest().getId())) {
        throw new BusinessException(400, "Question does not belong to this session's test");
      }
    }

    ToeicPassage group = null;
    if (request.getGroupId() != null) {
      group =
          passageRepository
              .findById(request.getGroupId())
              .orElseThrow(() -> new BusinessException(404, "Passage group not found"));

      if (group.getTest() == null || !group.getTest().getId().equals(session.getTest().getId())) {
        throw new BusinessException(400, "Passage group does not belong to this session's test");
      }
    }

    ToeicUserHighlight.HighlightData highlightData =
        ToeicUserHighlight.HighlightData.builder()
            .start(request.getStart())
            .end(request.getEnd())
            .text(request.getText())
            .color(request.getColor())
            .note(request.getNote())
            .build();

    ToeicUserHighlight highlight =
        ToeicUserHighlight.builder()
            .session(session)
            .question(question)
            .group(group)
            .highlightData(highlightData)
            .build();

    highlight = highlightRepository.save(highlight);
    return mapToHighlightDTO(highlight);
  }

  @Transactional
  public ToeicUserHighlightDTO updateHighlight(
      Long userId, Long highlightId, ToeicHighlightRequest request) {
    ToeicUserHighlight highlight =
        highlightRepository
            .findByIdAndSessionUserId(highlightId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Highlight not found or does not belong to the user"));

    ToeicUserHighlight.HighlightData highlightData = highlight.getHighlightData();
    if (highlightData == null) {
      highlightData = new ToeicUserHighlight.HighlightData();
    }

    if (request.getStart() != null) highlightData.setStart(request.getStart());
    if (request.getEnd() != null) highlightData.setEnd(request.getEnd());
    if (request.getText() != null) highlightData.setText(request.getText());
    if (request.getColor() != null) highlightData.setColor(request.getColor());
    if (request.getNote() != null) highlightData.setNote(request.getNote());

    highlight.setHighlightData(highlightData);
    highlight = highlightRepository.save(highlight);
    return mapToHighlightDTO(highlight);
  }

  @Transactional
  public void deleteHighlight(Long userId, Long highlightId) {
    ToeicUserHighlight highlight =
        highlightRepository
            .findByIdAndSessionUserId(highlightId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Highlight not found or does not belong to the user"));

    highlightRepository.delete(highlight);
  }

  @Transactional(readOnly = true)
  public List<ToeicUserHighlightDTO> getHighlightsBySession(Long userId, Long sessionId) {
    // Validate session ownership
    if (!sessionRepository.existsById(sessionId)) {
      throw new BusinessException(404, "Session not found");
    }

    List<ToeicUserHighlight> highlights =
        highlightRepository.findBySessionIdAndSessionUserId(sessionId, userId);

    return highlights.stream().map(this::mapToHighlightDTO).toList();
  }

  private ToeicUserHighlightDTO mapToHighlightDTO(ToeicUserHighlight highlight) {
    if (highlight == null) return null;
    return ToeicUserHighlightDTO.builder()
        .id(highlight.getId())
        .sessionId(highlight.getSession().getId())
        .questionId(highlight.getQuestion() != null ? highlight.getQuestion().getId() : null)
        .groupId(highlight.getGroup() != null ? highlight.getGroup().getId() : null)
        .start(
            highlight.getHighlightData() != null ? highlight.getHighlightData().getStart() : null)
        .end(highlight.getHighlightData() != null ? highlight.getHighlightData().getEnd() : null)
        .text(highlight.getHighlightData() != null ? highlight.getHighlightData().getText() : null)
        .color(
            highlight.getHighlightData() != null ? highlight.getHighlightData().getColor() : null)
        .note(highlight.getHighlightData() != null ? highlight.getHighlightData().getNote() : null)
        .createdAt(highlight.getCreatedAt())
        .build();
  }
}
