package com.todaii.english.client.toeic_tag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicTag;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;

  public List<ToeicTag> getAllTags() {
    return tagRepository.findAll();
  }

  public ToeicTag findById(Long id) {
    return tagRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Tag not found"));
  }
}
