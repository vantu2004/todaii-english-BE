package com.todaii.english.server.toeic_tag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.core.entity.ToeicTag;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.server.toeic_question.QuestionRepository;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;
  private final QuestionRepository questionRepository;

  public List<ToeicTag> getAllTags() {
    return tagRepository.findAll();
  }

  public ToeicTag findById(Long id) {
    return tagRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Tag not found"));
  }

  public ToeicTag create(String name) {
    String alias = AdminUtils.toAlias(name);
    if (tagRepository.existsByAlias(alias)) {
      throw new BusinessException(409, "Alias already exists: " + alias);
    }

    ToeicTag tag = ToeicTag.builder().name(name).alias(alias).build();

    return tagRepository.save(tag);
  }

  public ToeicTag update(Long id, String name) {
    ToeicTag tag = findById(id);
    String alias = AdminUtils.toAlias(name);

    // chỉ check khi alias thực sự thay đổi
    if (!alias.equals(tag.getAlias())) {
      boolean aliasExists = tagRepository.existsByAlias(alias);

      if (aliasExists) {
        throw new BusinessException(409, "Alias already exists: " + alias);
      }
    }

    tag.setName(name);
    tag.setAlias(alias);

    return tagRepository.save(tag);
  }

  public void delete(Long id) {
    ToeicTag tag = findById(id);

    List<ToeicQuestion> questions = questionRepository.findAllByTagsId(id);

    for (ToeicQuestion question : questions) {
      question.getTags().remove(tag);
    }

    tagRepository.delete(tag);
  }
}
