package com.todaii.english.server.toeic_question;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.request.server.toeic.BulkQuestionRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/question")
public class QuestionApiController {

  private final QuestionService questionService;

  @GetMapping
  public ResponseEntity<Page<ToeicQuestionDTO>> getAllPaged(
      @RequestParam(required = false) Long testId,
      @RequestParam(required = false) Long passageId,
      @RequestParam(required = false) List<Long> tagIds,
      Pageable pageable) {
    return ResponseEntity.ok(questionService.getAllPaged(testId, passageId, tagIds, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ToeicQuestionDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(questionService.getById(id));
  }

  @PostMapping
  public ResponseEntity<ToeicQuestionDTO> createQuestion(@Valid @RequestBody ToeicQuestionDTO dto) {
    return ResponseEntity.status(201).body(questionService.create(dto));
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<ToeicQuestion>> createBulk(
      @Valid @RequestBody BulkQuestionRequest request) {
    return ResponseEntity.status(201).body(questionService.createBulk(request.getQuestions()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ToeicQuestion> updateQuestion(
      @PathVariable Long id, @Valid @RequestBody ToeicQuestionDTO dto) {
    return ResponseEntity.ok(questionService.update(id, dto));
  }

  @PutMapping("/bulk")
  public ResponseEntity<List<ToeicQuestion>> updateBulk(
      @Valid @RequestBody BulkQuestionRequest request) {

    return ResponseEntity.ok(questionService.updateBulk(request.getQuestions()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
    questionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
