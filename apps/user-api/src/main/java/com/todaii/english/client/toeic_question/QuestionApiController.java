package com.todaii.english.client.toeic_question;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.dto.toeic.ToeicQuestionDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toeic")
public class QuestionApiController {
  private final QuestionService questionService;

  // nếu question thuộc về passage thì trả ToeicPassageDTO, ko thì trả QuestionDTO
  @GetMapping("/question/{questionId}")
  public ResponseEntity<Object> getQuestionById(@PathVariable Long questionId) {
    return ResponseEntity.ok(questionService.getQuestionDTOById(questionId));
  }

  @GetMapping("/test/{testId}/part/{partNumber}/question")
  public ResponseEntity<List<ToeicQuestionDTO>> getQuestionsByPartNumber(
      @PathVariable Long testId, @PathVariable Integer partNumber) {
    return ResponseEntity.ok(questionService.getAllQuestionsByPartNumber(testId, partNumber));
  }
}
