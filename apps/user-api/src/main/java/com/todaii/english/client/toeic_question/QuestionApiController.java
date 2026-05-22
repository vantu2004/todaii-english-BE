package com.todaii.english.client.toeic_question;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.dto.ToeicQuestionDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toeic")
public class QuestionApiController {
  private final QuestionService questionService;

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
}
