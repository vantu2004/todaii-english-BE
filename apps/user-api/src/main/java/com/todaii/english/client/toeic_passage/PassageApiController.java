package com.todaii.english.client.toeic_passage;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.dto.toeic.ToeicPassageDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toeic")
public class PassageApiController {
  private final PassageService passageService;

  @GetMapping("/test/{testId}/part/{partNumber}/passage")
  public ResponseEntity<List<ToeicPassageDTO>> getPassagesByPartNumber(
      @PathVariable Long testId, @PathVariable Integer partNumber) {
    return ResponseEntity.ok(passageService.getPassagesByPartNumber(testId, partNumber));
  }
}
