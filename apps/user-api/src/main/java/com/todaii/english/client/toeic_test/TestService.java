package com.todaii.english.client.toeic_test;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.shared.dto.ToeicTestDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
  private final ModelMapper modelMapper;
  private final TestRepository testRepository;

  public Page<ToeicTestDTO> getAllPublishedPaged(
      int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<ToeicTest> toeicTestPage = testRepository.getAllPublishedPaged(keyword, pageable);

    return toeicTestPage.map(toeicTest -> modelMapper.map(toeicTest, ToeicTestDTO.class));
  }

  public Page<ToeicTestDTO> findPublishedByCollectionId(
      Long collectionId, int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<ToeicTest> toeicTestPage =
        testRepository.findPublishedByCollectionId(collectionId, keyword, pageable);

    return toeicTestPage.map(toeicTest -> modelMapper.map(toeicTest, ToeicTestDTO.class));
  }

  public ToeicTestDTO getPublishedTestDTOById(Long id) {
    ToeicTest test = testRepository.findPublishedById(id)
        .orElseThrow(() -> new BusinessException(404, "TOEIC test not found"));
    return modelMapper.map(test, ToeicTestDTO.class);
  }
}
