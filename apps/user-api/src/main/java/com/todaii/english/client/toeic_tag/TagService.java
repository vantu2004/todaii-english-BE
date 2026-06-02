package com.todaii.english.client.toeic_tag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTag;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;

  public List<ToeicTag> findAllTagsByTestId(Long testId) {
    return tagRepository.findAllTagsByTestId(testId);
  }

  public List<ToeicQuestion> findQuestionsByTag(Long testId, Long tagId) {
    return tagRepository.findQuestionsByTag(testId, tagId);
  }
}
