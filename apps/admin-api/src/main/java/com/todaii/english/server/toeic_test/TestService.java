package com.todaii.english.server.toeic_test;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.toeic.ToeicCollection;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.server.toeic_collection.CollectionRepository;
import com.todaii.english.shared.dto.toeic.ToeicTestDTO;
import com.todaii.english.shared.enums.TestType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.ToeicTestRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
  private final ModelMapper modelMapper;
  private final TestRepository testRepository;
  private final CollectionRepository collectionRepository;
  private final CloudinaryPort cloudinaryPort;

  private ToeicTest findById(Long testId) {
    return testRepository
        .findById(testId)
        .orElseThrow(() -> new BusinessException(404, "Test not found"));
  }

  public Page<ToeicTestDTO> getAllPaged(
      int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<ToeicTest> toeicTestPage =
        testRepository.getAllPaged(AdminUtils.formatSearchKeyword(keyword), pageable);

    return toeicTestPage.map(toeicTest -> modelMapper.map(toeicTest, ToeicTestDTO.class));
  }

  public Page<ToeicTestDTO> findByCollectionId(
      Long collectionId, int page, int size, String sortBy, String direction, String keyword) {

    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<ToeicTest> toeicTestPage =
        testRepository.findByCollectionId(
            collectionId, AdminUtils.formatSearchKeyword(keyword), pageable);

    return toeicTestPage.map(toeicTest -> modelMapper.map(toeicTest, ToeicTestDTO.class));
  }

  public ToeicTestDTO getTestDTOById(Long id) {
    return modelMapper.map(findById(id), ToeicTestDTO.class);
  }

  public ToeicTestDTO create(ToeicTestRequest toeicTestRequest) {
    validateMedia(toeicTestRequest);

    ToeicCollection collection =
        collectionRepository
            .findById(toeicTestRequest.getCollectionId())
            .orElseThrow(() -> new BusinessException(404, "Collection not found"));

    ToeicTest toeicTest = new ToeicTest();

    mapRequestToEntity(toeicTestRequest, toeicTest);

    toeicTest.setCollection(collection);

    ToeicTest savedTest = testRepository.save(toeicTest);

    return modelMapper.map(savedTest, ToeicTestDTO.class);
  }

  public ToeicTestDTO update(Long id, ToeicTestRequest toeicTestRequest) {
    validateMedia(toeicTestRequest);

    ToeicTest toeicTest = findById(id);

    mapRequestToEntity(toeicTestRequest, toeicTest);

    if (toeicTestRequest.getCollectionId() != null) {
      ToeicCollection collection =
          collectionRepository
              .findById(toeicTestRequest.getCollectionId())
              .orElseThrow(() -> new BusinessException(404, "Collection not found"));
      toeicTest.setCollection(collection);
    }

    ToeicTest updatedToeicTest = testRepository.save(toeicTest);

    return modelMapper.map(updatedToeicTest, ToeicTestDTO.class);
  }

  private void mapRequestToEntity(ToeicTestRequest toeicTestRequest, ToeicTest toeicTest) {
    modelMapper.map(toeicTestRequest, toeicTest);

    if (toeicTestRequest.getImageRequest() != null) {
      String uploadedImageUrl = toeicTestRequest.getImageRequest().getUploadedImage();
      String manualImageUrl = toeicTestRequest.getImageRequest().getImageUrl();
      if (StringUtils.hasText(uploadedImageUrl)) {
        toeicTest.setImageUrl(uploadedImageUrl);
      } else if (StringUtils.hasText(manualImageUrl)) {
        toeicTest.setImageUrl(manualImageUrl);
      } else {
        toeicTest.setImageUrl(null);
      }
    }

    if (toeicTestRequest.getAudioRequest() != null) {
      String uploadedAudioUrl = toeicTestRequest.getAudioRequest().getUploadedAudio();
      String manualAudioUrl = toeicTestRequest.getAudioRequest().getAudioUrl();
      if (StringUtils.hasText(uploadedAudioUrl)) {
        toeicTest.setAudioUrl(uploadedAudioUrl);
      } else if (StringUtils.hasText(manualAudioUrl)) {
        toeicTest.setAudioUrl(manualAudioUrl);
      } else {
        toeicTest.setAudioUrl(null);
      }
    }
  }

  private void validateMedia(ToeicTestRequest request) {
    validateImage(request);
    validateAudio(request);
  }

  private void validateImage(ToeicTestRequest request) {
    if (request.getImageRequest() == null
        || (!StringUtils.hasText(request.getImageRequest().getUploadedImage())
            && !StringUtils.hasText(request.getImageRequest().getImageUrl()))) {
      throw new BusinessException(400, "Image is required");
    }
  }

  private void validateAudio(ToeicTestRequest request) {
    boolean hasAudio =
        request.getAudioRequest() != null
            && (StringUtils.hasText(request.getAudioRequest().getUploadedAudio())
                || StringUtils.hasText(request.getAudioRequest().getAudioUrl()));

    if (request.getTestType() == TestType.TOEIC_LR && !hasAudio) {
      throw new BusinessException(400, "Audio is required");
    }
  }

  public void delete(Long id) {
    ToeicTest toeicTest = findById(id);

    if (StringUtils.hasText(toeicTest.getImageUrl())) {
      cloudinaryPort.deleteFile(toeicTest.getImageUrl());
    }
    if (StringUtils.hasText(toeicTest.getAudioUrl())) {
      cloudinaryPort.deleteFile(toeicTest.getAudioUrl());
    }

    testRepository.deleteById(id);
  }
}
