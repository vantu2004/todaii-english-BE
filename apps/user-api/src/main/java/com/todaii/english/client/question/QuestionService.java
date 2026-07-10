package com.todaii.english.client.question;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Question;
import com.todaii.english.shared.enums.TopicType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
  private final QuestionRepository questionRepository;

  public List<Question> getQuestionsByTargetId(TopicType topicType, Long targetId) {
    return questionRepository.findByTopicTypeAndContentId(topicType, targetId);
  }
}
