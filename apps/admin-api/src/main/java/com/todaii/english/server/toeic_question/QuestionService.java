package com.todaii.english.server.toeic_question;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTag;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.server.toeic_tag.TagRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.Part12Request;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
  private final QuestionRepository questionRepository;
  private final CloudinaryPort cloudinaryPort;
  private final ModelMapper modelMapper;
  private final TestRepository testRepository;
  private final TagRepository tagRepository;

  // ----- DÙNG CHUNG -----

  private ToeicQuestion findById(Long questionId) {
    return questionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(404, "Question not found"));
  }

  public ToeicQuestionDTO getQuestionDTOById(Long questionId) {
    return modelMapper.map(findById(questionId), ToeicQuestionDTO.class);
  }

  public List<ToeicQuestionDTO> getAllQuestionsByPartNumber(Long testId, Integer partNumber) {
    return questionRepository.findByTestIdAndPartNumber(testId, partNumber).stream()
        .map(toeicQuestion -> modelMapper.map(toeicQuestion, ToeicQuestionDTO.class))
        .toList();
  }

  public void deleteQuestion(Long questionId) {
    ToeicQuestion question = findById(questionId);

    if (StringUtils.hasText(question.getImageUrl())) {
      cloudinaryPort.deleteFile(question.getImageUrl());
    }
    if (StringUtils.hasText(question.getAudioUrl())) {
      cloudinaryPort.deleteFile(question.getAudioUrl());
    }

    questionRepository.deleteById(questionId);
  }

  // ----- PART 01 + 02 -----

  public ToeicQuestionDTO createPart12Question(
      Long testId, Integer partNumber, Part12Request request) {
    validateMedia(request, partNumber);

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

  public ToeicQuestionDTO updatePart12Question(Long questionId, Part12Request request) {
    ToeicQuestion toeicQuestion = findById(questionId);

    validateMedia(request, toeicQuestion.getPartNumber());

    // SELECT * FROM toeic_tags WHERE id IN (1,2,3)
    mapRequestToEntity(request, toeicQuestion);

    ToeicQuestion savedQuestion = questionRepository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }

  private void validateMedia(Part12Request request, Integer partNumber) {
    switch (partNumber) {
      case 1 -> {
        validateImage(request);
        validateAudio(request);
      }

      case 2 -> validateAudio(request);
    }
  }

  private void validateImage(Part12Request request) {
    if (!StringUtils.hasText(request.getImageRequest().getUploadedImage())
        && !StringUtils.hasText(request.getImageRequest().getImageUrl())) {
      throw new BusinessException(400, "Image is required");
    }
  }

  private void validateAudio(Part12Request request) {
    if (!StringUtils.hasText(request.getAudioRequest().getUploadedAudio())
        && !StringUtils.hasText(request.getAudioRequest().getAudioUrl())) {
      throw new BusinessException(400, "Audio is required");
    }
  }

  private void mapRequestToEntity(Part12Request request, ToeicQuestion toeicQuestion) {
    modelMapper.map(request, toeicQuestion);

    Set<ToeicTag> toeicTags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));

    toeicQuestion.setTags(toeicTags);

    // ưu tiên dùng url ảnh đã upload
    String imageUrl = request.getImageRequest().getUploadedImage();
    if (StringUtils.hasText(imageUrl)) {
      toeicQuestion.setImageUrl(imageUrl);
    }

    // ưu tiên dùng url audio đã upload
    String audioUrl = request.getAudioRequest().getUploadedAudio();
    if (StringUtils.hasText(audioUrl)) {
      toeicQuestion.setAudioUrl(audioUrl);
    }
  }
}
