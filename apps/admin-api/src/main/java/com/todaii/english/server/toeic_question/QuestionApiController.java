package com.todaii.english.server.toeic_question;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.dto.ToeicQuestionDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/question")
public class QuestionApiController {
  private final QuestionService questionService;

  @GetMapping("/{questionId}")
  public ResponseEntity<ToeicQuestionDTO> getQuestionById(@PathVariable Long questionId) {
    ToeicQuestionDTO toeicQuestionDTO = questionService.getQuestionById(questionId);

    return ResponseEntity.ok(toeicQuestionDTO);
  }

  @DeleteMapping("/{questionId}")
  public ResponseEntity<Void> deletePart01Question(@PathVariable Long questionId) {
    questionService.deleteQuestion(questionId);

    return ResponseEntity.noContent().build();
  }
}
