package com.todaii.english.server.toeic_question;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.toeic.ToeicPassage;
import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTag;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.server.toeic_passage.PassageRepository;
import com.todaii.english.server.toeic_tag.TagRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.Part12Request;
import com.todaii.english.shared.request.server.toeic.Part34567Request;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
  private final QuestionRepository questionRepository;
  private final CloudinaryPort cloudinaryPort;
  private final ModelMapper modelMapper;
  private final TestRepository testRepository;
  private final TagRepository tagRepository;
  private final PassageRepository passageRepository;

  // ----- DÙNG CHUNG -----

  private ToeicQuestion findById(Long questionId) {
    return questionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(404, "Question not found"));
  }

  private ToeicTest findToeicTestById(Long testId) {
    return testRepository
        .findById(testId)
        .orElseThrow(() -> new BusinessException(404, "Test not found"));
  }

  private Set<ToeicTag> getToeicTags(Set<Long> tagIds) {
    // SELECT * FROM toeic_tags WHERE id IN (1,2,3)
    Set<ToeicTag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
    if (tags.size() != tagIds.size()) {
      throw new BusinessException(404, "One or more tags do not exist");
    }

    return tags;
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
    ToeicTest toeicTest = findToeicTestById(testId);

    ToeicQuestion toeicQuestion = new ToeicQuestion();

    mapPart12RequestToEntity(request, partNumber, toeicQuestion);

    toeicQuestion.setPartNumber(partNumber);
    toeicQuestion.setTest(toeicTest);

    ToeicQuestion savedQuestion = questionRepository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }

  public ToeicQuestionDTO updatePart12Question(Long questionId, Part12Request request) {
    ToeicQuestion toeicQuestion = findById(questionId);

    mapPart12RequestToEntity(request, toeicQuestion.getPartNumber(), toeicQuestion);

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
    if (request.getImageRequest() == null
        || (!StringUtils.hasText(request.getImageRequest().getUploadedImage())
        && !StringUtils.hasText(request.getImageRequest().getImageUrl()))) {
      throw new BusinessException(400, "Image is required");
    }
  }

  private void validateAudio(Part12Request request) {
    if (request.getAudioRequest() == null
        || (!StringUtils.hasText(request.getAudioRequest().getUploadedAudio())
        && !StringUtils.hasText(request.getAudioRequest().getAudioUrl()))) {
      throw new BusinessException(400, "Audio is required");
    }
  }

  private void mapPart12RequestToEntity(
      Part12Request request, Integer partNumber, ToeicQuestion toeicQuestion) {
    validateMedia(request, partNumber);

    modelMapper.map(request, toeicQuestion);

    toeicQuestion.setTags(getToeicTags(request.getTagIds()));

    if (request.getImageRequest() != null) {
      String uploadedImageUrl = request.getImageRequest().getUploadedImage();
      String manualImageUrl = request.getImageRequest().getImageUrl();
      if (StringUtils.hasText(uploadedImageUrl)) {
        toeicQuestion.setImageUrl(uploadedImageUrl);
      } else if (StringUtils.hasText(manualImageUrl)) {
        toeicQuestion.setImageUrl(manualImageUrl);
      } else {
        toeicQuestion.setImageUrl(null);
      }
    }

    if (request.getAudioRequest() != null) {
      String uploadedAudioUrl = request.getAudioRequest().getUploadedAudio();
      String manualAudioUrl = request.getAudioRequest().getAudioUrl();
      if (StringUtils.hasText(uploadedAudioUrl)) {
        toeicQuestion.setAudioUrl(uploadedAudioUrl);
      } else if (StringUtils.hasText(manualAudioUrl)) {
        toeicQuestion.setAudioUrl(manualAudioUrl);
      } else {
        toeicQuestion.setAudioUrl(null);
      }
    }
  }

  // ----- PART 03 + 04 + 05 + 06 + 07 -----

  public ToeicQuestionDTO createPart34567Question(
      Long testId, Integer partNumber, Part34567Request request) {
    ToeicTest toeicTest = findToeicTestById(testId);

    ToeicQuestion toeicQuestion = new ToeicQuestion();

    // chỉ set khi create, update ko set
    ToeicPassage toeicPassage = validateAndGetPassage(partNumber, request.getPassageId());

    mapPart34567RequestToEntity(request, partNumber, toeicQuestion);

    toeicQuestion.setPartNumber(partNumber);
    toeicQuestion.setTest(toeicTest);
    toeicQuestion.setPassage(toeicPassage);

    ToeicQuestion savedQuestion = questionRepository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }

  public ToeicQuestionDTO updatePart34567Question(Long questionId, Part34567Request request) {
    ToeicQuestion toeicQuestion = findById(questionId);

    mapPart34567RequestToEntity(request, toeicQuestion.getPartNumber(), toeicQuestion);

    ToeicQuestion savedQuestion = questionRepository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }

  private void mapPart34567RequestToEntity(
      Part34567Request request, Integer partNumber, ToeicQuestion toeicQuestion) {
    validateQuestion(partNumber, request.getQuestion());

    modelMapper.map(request, toeicQuestion);

    toeicQuestion.setTags(getToeicTags(request.getTagIds()));
  }

  private ToeicPassage validateAndGetPassage(Integer partNumber, Long passageId) {
    if (partNumber == 5) {
      return null;
    }

    if (passageId == null) {
      throw new BusinessException(400, "Passage ID is required");
    }

    ToeicPassage toeicPassage =
        passageRepository
            .findById(passageId)
            .orElseThrow(() -> new BusinessException(404, "Passage not found"));

    if (!toeicPassage.getPartNumber().equals(partNumber)) {
      throw new BusinessException(400, "Passage part number does not match question part number");
    }

    return toeicPassage;
  }

  private void validateQuestion(Integer partNumber, String question) {
    if (partNumber != 6 && !StringUtils.hasText(question)) {
      throw new BusinessException(400, "Question is required for TOEIC parts 3, 4, 5, and 7");
    }
  }
}
