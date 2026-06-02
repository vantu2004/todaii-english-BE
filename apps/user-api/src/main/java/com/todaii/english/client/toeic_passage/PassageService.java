package com.todaii.english.client.toeic_passage;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.todaii.english.shared.dto.ToeicPassageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {
  private final PassageRepository passageRepository;
  private final ModelMapper modelMapper;

  public List<ToeicPassageDTO> getPassagesByPartNumber(Long testId, Integer partNumber) {
    return passageRepository
        .findByTestIdAndPartNumberOrderByCreatedAtAsc(testId, partNumber)
        .stream()
        .map(toeicPassage -> modelMapper.map(toeicPassage, ToeicPassageDTO.class))
        .toList();
  }
}
