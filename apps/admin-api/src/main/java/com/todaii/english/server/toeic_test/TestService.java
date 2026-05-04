package com.todaii.english.server.toeic_test;

import com.todaii.english.shared.response.ToeicTestResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.admin.AdminRepository;
import com.todaii.english.server.toeic_collection.CollectionRepository;
import com.todaii.english.shared.request.server.ToeicTestRequest;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
  private final ModelMapper modelMapper;
  private final TestRepository testRepository;
  private final CollectionRepository collectionRepository;
  private final AdminRepository adminRepository;

//  public Page<ToeicTestRequest> getAllPaged(Long collectionId, Pageable pageable) {
//
//    Page<ToeicTest> page;
//
//    if (collectionId != null) {
//      page = testRepository.findByCollectionId(collectionId, pageable);
//    } else {
//      page = testRepository.findAll(pageable);
//    }
//
//    return page.map(this::toDTO);
//  }

  public ToeicTestResponse getById(Long id) {
    ToeicTest toeicTest = testRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(404, "Test not found"));

    return modelMapper.map(toeicTest, ToeicTestResponse.class);
  }

  public ToeicTestResponse create(Long adminId, ToeicTestRequest dto) {
    ToeicCollection collection =
        collectionRepository
            .findById(dto.getCollectionId())
            .orElseThrow(() -> new BusinessException(404, "Collection not found"));

    ToeicTest test = modelMapper.map(dto, ToeicTest.class);
    test.setCollection(collection);

    ToeicTest savedTest = testRepository.save(test);

    return modelMapper.map(savedTest, ToeicTestResponse.class);
  }

//  public ToeicTest update(Long id, ToeicTestRequest dto) {
//
//    ToeicTest test =
//        testRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Test not found"));
//
//    modelMapper.map(dto, test);
//
//    if (dto.getCollectionId() != null) {
//      ToeicCollection collection =
//          collectionRepository
//              .findById(dto.getCollectionId())
//              .orElseThrow(() -> new BusinessException(404, "Collection not found"));
//      test.setCollection(collection);
//    }
//
//    return testRepository.save(test);
//  }
//
//  public void delete(Long id) {
//    ToeicTest test =
//        testRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Test not found"));
//
//    testRepository.delete(test);
//  }
//
//  private ToeicTestRequest toDTO(ToeicTest entity) {
//
//    ToeicTestRequest dto = modelMapper.map(entity, ToeicTestRequest.class);
//
//    if (entity.getCollection() != null) {
//      dto.setCollectionId(entity.getCollection().getId());
//      dto.setCollectionName(entity.getCollection().getName());
//    }
//
//    return dto;
//  }
}
