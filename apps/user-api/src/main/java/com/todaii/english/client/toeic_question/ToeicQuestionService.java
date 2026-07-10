package com.todaii.english.client.toeic_question;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.shared.dto.toeic.ToeicPassageDTO;
import com.todaii.english.shared.dto.toeic.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToeicQuestionService {
  private final ToeicQuestionRepository questionRepository;
  private final ModelMapper modelMapper;

  public ToeicQuestion findById(Long questionId) {
    return questionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(404, "Question not found"));
  }

  public Object getQuestionDTOById(Long questionId) {
    ToeicQuestion toeicQuestion = findById(questionId);
    if (toeicQuestion.getPassage() != null) {
      ToeicPassageDTO toeicPassageDTO =
          modelMapper.map(toeicQuestion.getPassage(), ToeicPassageDTO.class);
      toeicPassageDTO.setQuestions(List.of(modelMapper.map(toeicQuestion, ToeicQuestionDTO.class)));

      return toeicPassageDTO;
    }

    return modelMapper.map(toeicQuestion, ToeicQuestionDTO.class);
  }

  public List<ToeicQuestionDTO> getAllQuestionsByPartNumber(Long testId, Integer partNumber) {
    return questionRepository
        .findByTestIdAndPartNumberOrderByCreatedAtAsc(testId, partNumber)
        .stream()
        .map(toeicQuestion -> modelMapper.map(toeicQuestion, ToeicQuestionDTO.class))
        .toList();
  }
}
