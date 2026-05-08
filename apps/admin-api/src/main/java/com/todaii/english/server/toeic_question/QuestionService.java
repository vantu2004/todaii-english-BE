package com.todaii.english.server.toeic_question;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
  private final QuestionRepository questionRepository;
  private final CloudinaryPort cloudinaryPort;
  private final ModelMapper modelMapper;

  private ToeicQuestion findById(Long questionId) {
    return questionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(404, "Question not found"));
  }

  public ToeicQuestionDTO getQuestionById(Long questionId) {
    return modelMapper.map(findById(questionId), ToeicQuestionDTO.class);
  }

  public void deleteQuestion(Long questionId) {
    ToeicQuestion question = findById(questionId);

    cloudinaryPort.deleteFile(question.getImageUrl());
    cloudinaryPort.deleteFile(question.getAudioUrl());

    questionRepository.deleteById(questionId);
  }
}
