package com.todaii.english.client.toeic_passage;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicPassage;
import com.todaii.english.shared.dto.ToeicPassageDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {
  private final PassageRepository passageRepository;
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
}
