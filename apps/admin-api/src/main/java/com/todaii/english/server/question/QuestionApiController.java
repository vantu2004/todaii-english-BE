package com.todaii.english.server.question;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Question;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.enums.TopicType;
import com.todaii.english.shared.request.server.QuestionRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/questions")
public class QuestionApiController {
  private final QuestionService questionService;

  @GetMapping
  public ResponseEntity<List<Question>> getAllQuestions(
      @RequestParam TopicType topicType, @RequestParam Long targetId) {
    return ResponseEntity.ok(questionService.getAllQuestions(topicType, targetId));
  }

  @PostMapping
  public ResponseEntity<Question> createQuestion(@Valid @RequestBody QuestionRequest request) {
    return ResponseEntity.status(201).body(questionService.createQuestion(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Question> updateQuestion(
      @PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
    return ResponseEntity.ok(questionService.updateQuestion(id, request));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
    questionService.deleteQuestion(id);

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/auto-generate")
  public ResponseEntity<List<Question>> autoGenerateQuestions(
      Authentication authentication,
      @RequestParam TopicType topicType,
      @RequestParam Long targetId,
      @Min(value = 1, message = "Count must be at least 1")
          @Max(value = 10, message = "Count cannot exceed 10")
          int count) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

    return ResponseEntity.ok(
        questionService.generateQuestions(topicType, targetId, count, currentAdminId));
  }
}
