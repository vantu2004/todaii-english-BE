package com.todaii.english.client.question;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Question;
import com.todaii.english.shared.enums.TopicType;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/questions")
public class QuestionApiController {
  private final QuestionService questionService;

  @GetMapping
  public ResponseEntity<List<Question>> getQuestionsByTargetId(
      @RequestParam TopicType topicType, @RequestParam Long targetId) {
    return ResponseEntity.ok(questionService.getQuestionsByTargetId(topicType, targetId));
  }
}
