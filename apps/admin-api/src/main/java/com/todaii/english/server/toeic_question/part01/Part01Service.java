package com.todaii.english.server.toeic_question.part01;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTag;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.server.toeic_question.QuestionRepository;
import com.todaii.english.server.toeic_tag.TagRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.Part01Request;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Part01Service {
  private final QuestionRepository questionRepository;
  private final TestRepository testRepository;
  private final TagRepository tagRepository;
  private final ModelMapper modelMapper;

  private ToeicQuestion findById(Long questionId) {
    return questionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(404, "Question not found"));
  }

  public List<ToeicQuestionDTO> getAllQuestionsByPartNumber(Long testId, Integer partNumber) {
    return questionRepository.findByTestIdAndPartNumber(testId, partNumber).stream()
        .map(toeicQuestion -> modelMapper.map(toeicQuestion, ToeicQuestionDTO.class))
        .toList();
  }

  public ToeicQuestionDTO createQuestion(Long testId, Integer partNumber, Part01Request request) {

    switch (partNumber) {
      case 1 -> {
        validateImage(request);
        validateAudio(request);
      }

      case 2 -> validateAudio(request);
    }

    ToeicTest toeicTest =
        testRepository
            .findById(testId)
            .orElseThrow(() -> new BusinessException(404, "Test not found"));

    ToeicQuestion toeicQuestion = new ToeicQuestion();

    mapRequestToEntity(request, toeicQuestion);

    toeicQuestion.setPartNumber(partNumber);
    toeicQuestion.setTest(toeicTest);

    ToeicQuestion savedQuestion = questionRepository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }

  private void validateImage(Part01Request request) {
    if (!StringUtils.hasText(request.getImageRequest().getUploadedImage())
        && !StringUtils.hasText(request.getImageRequest().getImageUrl())) {
      throw new BusinessException(400, "Image is required");
    }
  }

  private void validateAudio(Part01Request request) {
    if (!StringUtils.hasText(request.getAudioRequest().getUploadedAudio())
        && !StringUtils.hasText(request.getAudioRequest().getAudioUrl())) {
      throw new BusinessException(400, "Audio is required");
    }
  }

  public ToeicQuestionDTO updateQuestion(
      Integer partNumber, Long questionId, @Valid Part01Request request) {
    ToeicQuestion toeicQuestion = findById(questionId);

    switch (partNumber) {
      case 1 -> {
        validateImage(request);
        validateAudio(request);
      }

      case 2 -> validateAudio(request);
    }

    // SELECT * FROM toeic_tags WHERE id IN (1,2,3)
    mapRequestToEntity(request, toeicQuestion);

    ToeicQuestion savedQuestion = questionRepository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }

  private void mapRequestToEntity(Part01Request request, ToeicQuestion toeicQuestion) {
    modelMapper.map(request, toeicQuestion);

    Set<ToeicTag> toeicTags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));

    toeicQuestion.setTags(toeicTags);

    // ưu tiên dùng url ảnh đã upload
    if (StringUtils.hasText(request.getImageRequest().getUploadedImage())) {
      toeicQuestion.setImageUrl(request.getImageRequest().getUploadedImage());
    }

    // ưu tiên dùng url audio đã upload
    if (StringUtils.hasText(request.getAudioRequest().getUploadedAudio())) {
      toeicQuestion.setAudioUrl(request.getAudioRequest().getUploadedAudio());
    }
  }
}
