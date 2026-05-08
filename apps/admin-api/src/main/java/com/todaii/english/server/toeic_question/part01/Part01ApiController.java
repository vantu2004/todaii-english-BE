package com.todaii.english.server.toeic_question.part01;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.request.server.toeic.Part01Request;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toeic/test/{testId}/part/{partNumber}/question")
public class Part01ApiController {
  private final Part01Service part01Service;

  @GetMapping("/{questionId}")
  public ResponseEntity<ToeicQuestionDTO> getQuestionById(@PathVariable Long questionId) {
    ToeicQuestionDTO toeicQuestionDTO = part01Service.getQuestionById(questionId);

    return ResponseEntity.ok(toeicQuestionDTO);
  }

  @GetMapping
  public ResponseEntity<List<ToeicQuestionDTO>> getQuestionsByPartNumber(
      @PathVariable Long testId, @PathVariable Integer partNumber) {
    return ResponseEntity.ok(part01Service.getAllQuestionsByPartNumber(testId, partNumber));
  }

  @PostMapping
  public ResponseEntity<ToeicQuestionDTO> createPart01Question(
      @PathVariable Long testId,
      @PathVariable Integer partNumber,
      @Valid @RequestBody Part01Request request) {
    ToeicQuestionDTO created = part01Service.createQuestion(testId, partNumber, request);

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  //  @PutMapping("/{id}")
  //  public ResponseEntity<ToeicQuestionDTO> updatePart01Question(
  //      @PathVariable Long id, @Valid @RequestBody Part01Request request) {
  //    ToeicQuestionDTO updated = part01Service.updateQuestion(id, request);
  //
  //    return ResponseEntity.ok(updated);
  //  }
  //
  //  @DeleteMapping("/{id}")
  //  public ResponseEntity<Void> deletePart01Question(@PathVariable Long id) {
  //    part01Service.deleteQuestion(id);
  //    return ResponseEntity.noContent().build();
  //  }
}
