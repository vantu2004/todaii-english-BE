package com.todaii.english.server.toeic_question.part01;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.core.entity.ToeicTag;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_tag.TagRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.Part01Request;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Part01Service {
  private final Part01Repository part01Repository;
  private final TestRepository testRepository;
  private final TagRepository tagRepository;
  private final ModelMapper modelMapper;

  public ToeicQuestionDTO getQuestionById(Long id) {
    ToeicQuestion question =
        part01Repository
            .findById(id)
            .orElseThrow(() -> new BusinessException(404, "Question not found"));

    return modelMapper.map(question, ToeicQuestionDTO.class);
  }

  public List<ToeicQuestionDTO> getAllQuestionsByPartNumber(Long testId, Integer partNumber) {
    return part01Repository.findByTestIdAndPartNumber(testId, partNumber).stream()
        .map(toeicQuestion -> modelMapper.map(toeicQuestion, ToeicQuestionDTO.class))
        .toList();
  }

  public ToeicQuestionDTO createQuestion(Long testId, Integer partNumber, Part01Request request) {
    ToeicTest toeicTest =
        testRepository
            .findById(testId)
            .orElseThrow(() -> new BusinessException(404, "Test not found"));

    // SELECT * FROM toeic_tags WHERE id IN (1,2,3)
    Set<ToeicTag> toeicTags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));

    ToeicQuestion toeicQuestion = modelMapper.map(request, ToeicQuestion.class);
    toeicQuestion.setPartNumber(partNumber);
    toeicQuestion.setTest(toeicTest);
    toeicQuestion.setTags(toeicTags);

    // ưu tiên dùng url ảnh đã upload
    if (StringUtils.hasText(request.getUploadedImage())) {
      toeicQuestion.setImageUrl(request.getUploadedImage());
    }

    // ưu tiên dùng url audio đã upload
    if (StringUtils.hasText(request.getUploadedAudio())) {
      toeicQuestion.setAudioUrl(request.getAudioUrl());
    }

    ToeicQuestion savedQuestion = part01Repository.save(toeicQuestion);

    return modelMapper.map(savedQuestion, ToeicQuestionDTO.class);
  }
}
