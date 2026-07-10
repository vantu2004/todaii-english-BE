package com.todaii.english.client.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Question;
import com.todaii.english.shared.enums.TopicType;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
  List<Question> findByTopicTypeAndContentId(TopicType topicType, Long contentId);
}
