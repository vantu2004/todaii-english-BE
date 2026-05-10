package com.todaii.english.server.toeic_passage;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.toeic.ToeicPassage;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicPassageDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.ToeicPassageRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {
  private final PassageRepository passageRepository;
  private final TestRepository testRepository;
  private final CloudinaryPort cloudinaryPort;
  private final ModelMapper modelMapper;

  public ToeicPassage findById(Long passageId) {
    return passageRepository
        .findById(passageId)
        .orElseThrow(() -> new BusinessException(404, "Toeic passage not found"));
  }

  public List<ToeicPassageDTO> getPassagesByPartNumber(Long testId, Integer partNumber) {
    return passageRepository.findByTestIdAndPartNumber(testId, partNumber).stream()
        .map(toeicPassage -> modelMapper.map(toeicPassage, ToeicPassageDTO.class))
        .toList();
  }

  public ToeicPassageDTO getPassageDTOById(Long passageId) {
    ToeicPassage toeicPassage = findById(passageId);

    return modelMapper.map(toeicPassage, ToeicPassageDTO.class);
  }

  public ToeicPassageDTO createPassage(
      Long testId, Integer partNumber, ToeicPassageRequest request) {
    validatePassagePart(partNumber);
    validateAudio(partNumber, request);

    ToeicTest toeicTest =
        testRepository
            .findById(testId)
            .orElseThrow(() -> new BusinessException(404, "Test not found"));

    ToeicPassage toeicPassage = new ToeicPassage();

    mapRequestToEntity(request, toeicPassage);

    toeicPassage.setPartNumber(partNumber);
    toeicPassage.setTest(toeicTest);

    ToeicPassage savedPassage = passageRepository.save(toeicPassage);

    return modelMapper.map(savedPassage, ToeicPassageDTO.class);
  }

  public ToeicPassageDTO updatePassage(Long passageId, ToeicPassageRequest request) {
    ToeicPassage toeicPassage = findById(passageId);

    validateAudio(toeicPassage.getPartNumber(), request);

    mapRequestToEntity(request, toeicPassage);

    ToeicPassage savedPassage = passageRepository.save(toeicPassage);

    return modelMapper.map(savedPassage, ToeicPassageDTO.class);
  }

  private void mapRequestToEntity(ToeicPassageRequest request, ToeicPassage passage) {
    modelMapper.map(request, passage);

    // ưu tiên uploaded image
    if (StringUtils.hasText(request.getImageRequest().getUploadedImage())) {
      passage.setImageUrl(request.getImageRequest().getUploadedImage());
    }

    // ưu tiên uploaded audio
    if (StringUtils.hasText(request.getAudioRequest().getUploadedAudio())) {
      passage.setAudioUrl(request.getAudioRequest().getUploadedAudio());
    }
  }

  private void validateAudio(Integer partNumber, ToeicPassageRequest request) {
    boolean hasAudio =
        StringUtils.hasText(request.getAudioRequest().getUploadedAudio())
            || StringUtils.hasText(request.getAudioRequest().getAudioUrl());

    if ((partNumber == 3 || partNumber == 4) && !hasAudio) {
      throw new BusinessException(400, "Audio is required");
    }
  }

  private void validatePassagePart(Integer partNumber) {
    if (partNumber != 3 && partNumber != 4 && partNumber != 6 && partNumber != 7) {

      throw new BusinessException(400, "Passage is only supported for TOEIC parts 3, 4, 6, and 7");
    }
  }

  public void deletePassage(Long passageId) {
    ToeicPassage toeicPassage = findById(passageId);

    if (StringUtils.hasText(toeicPassage.getImageUrl())) {
      cloudinaryPort.deleteFile(toeicPassage.getImageUrl());
    }
    if (StringUtils.hasText(toeicPassage.getAudioUrl())) {
      cloudinaryPort.deleteFile(toeicPassage.getAudioUrl());
    }

    passageRepository.deleteById(passageId);
  }
}
