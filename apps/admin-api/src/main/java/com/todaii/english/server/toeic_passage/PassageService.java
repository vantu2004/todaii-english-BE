package com.todaii.english.server.toeic_passage;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.ToeicPassage;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicPassageDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {

  private final PassageRepository passageRepository;
  private final TestRepository testRepository;
  private final ModelMapper modelMapper;

  public Page<ToeicPassageDTO> getAllPaged(Long testId, Pageable pageable) {

    Page<ToeicPassage> page;

    if (testId != null) {
      page = passageRepository.findByTestId(testId, pageable);
    } else {
      page = passageRepository.findAll(pageable);
    }

    return page.map(this::toDTO);
  }

  public ToeicPassageDTO getById(Long id) {
    return toDTO(findById(id));
  }

  public ToeicPassage findById(Long id) {
    return passageRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Question group not found"));
  }

  public ToeicPassage create(ToeicPassageDTO dto) {
    ToeicPassage group = modelMapper.map(dto, ToeicPassage.class);
    setTest(group, dto.getTestId());
    return passageRepository.save(group);
  }

  public ToeicPassage update(Long id, ToeicPassageDTO dto) {
    dto.setId(id);
    ToeicPassage group = findById(id);
    modelMapper.map(dto, group);
    setTest(group, dto.getTestId());
    return passageRepository.save(group);
  }

  public void delete(Long id) {
    ToeicPassage group = findById(id);
    passageRepository.delete(group);
  }

  private void setTest(ToeicPassage group, Long testId) {
    if (testId != null) {
      ToeicTest test =
          testRepository
              .findById(testId)
              .orElseThrow(() -> new BusinessException(404, "Test not found"));
      group.setTest(test);
    }
  }

  private ToeicPassageDTO toDTO(ToeicPassage entity) {
    ToeicPassageDTO dto = modelMapper.map(entity, ToeicPassageDTO.class);

    if (entity.getTest() != null) {
      dto.setTestId(entity.getTest().getId());
      dto.setTestTitle(entity.getTest().getTitle());
    }

    return dto;
  }
}
