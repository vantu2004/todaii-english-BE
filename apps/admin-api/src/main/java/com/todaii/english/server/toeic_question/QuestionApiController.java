package com.todaii.english.server.toeic_question;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.request.server.toeic.Part12Request;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic")
public class QuestionApiController {
  private final QuestionService questionService;

  // ----- DÙNG CHUNG -----

  @GetMapping("/question/{questionId}")
  public ResponseEntity<ToeicQuestionDTO> getQuestionById(@PathVariable Long questionId) {
    ToeicQuestionDTO toeicQuestionDTO = questionService.getQuestionDTOById(questionId);

    return ResponseEntity.ok(toeicQuestionDTO);
  }

  @GetMapping("/test/{testId}/part/{partNumber}/question")
  public ResponseEntity<List<ToeicQuestionDTO>> getQuestionsByPartNumber(
      @PathVariable Long testId, @PathVariable Integer partNumber) {
    return ResponseEntity.ok(questionService.getAllQuestionsByPartNumber(testId, partNumber));
  }

  @DeleteMapping("/question/{questionId}")
  public ResponseEntity<Void> deletePart01Question(@PathVariable Long questionId) {
    questionService.deleteQuestion(questionId);

    return ResponseEntity.noContent().build();
  }

  // ----- PART 01 + 02 -----

  @PostMapping("/test/{testId}/part/{partNumber}/question")
  public ResponseEntity<ToeicQuestionDTO> createPart12Question(
      @PathVariable Long testId,
      @PathVariable Integer partNumber,
      @Valid @RequestBody Part12Request request) {
    ToeicQuestionDTO created = questionService.createPart12Question(testId, partNumber, request);

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/question/{questionId}")
  public ResponseEntity<ToeicQuestionDTO> updatePart12Question(
      @PathVariable Long questionId, @Valid @RequestBody Part12Request request) {
    ToeicQuestionDTO updated = questionService.updatePart12Question(questionId, request);

    return ResponseEntity.ok(updated);
  }
}
