package com.todaii.english.server.toeic_passage;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.dto.toeic.ToeicPassageDTO;
import com.todaii.english.shared.request.server.toeic.ToeicPassageRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic")
public class PassageApiController {
  private final PassageService passageService;

  @GetMapping("/test/{testId}/part/{partNumber}/passage")
  public ResponseEntity<List<ToeicPassageDTO>> getPassagesByPartNumber(
      @PathVariable Long testId, @PathVariable Integer partNumber) {
    return ResponseEntity.ok(passageService.getPassagesByPartNumber(testId, partNumber));
  }

  @GetMapping("/passage/{passageId}")
  public ResponseEntity<ToeicPassageDTO> findById(@PathVariable Long passageId) {
    ToeicPassageDTO toeicPassageDTO = passageService.getPassageDTOById(passageId);

    return ResponseEntity.ok(toeicPassageDTO);
  }

  @PostMapping("/test/{testId}/part/{partNumber}/passage")
  public ResponseEntity<ToeicPassageDTO> createPassage(
      @PathVariable Long testId,
      @PathVariable Integer partNumber,
      @Valid @RequestBody ToeicPassageRequest toeicPassageRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(passageService.createPassage(testId, partNumber, toeicPassageRequest));
  }

  @PutMapping("/passage/{passageId}")
  public ResponseEntity<ToeicPassageDTO> updatePassage(
      @PathVariable Long passageId, @Valid @RequestBody ToeicPassageRequest toeicPassageRequest) {
    return ResponseEntity.ok(passageService.updatePassage(passageId, toeicPassageRequest));
  }

  @DeleteMapping("/passage/{passageId}")
  public ResponseEntity<Void> deletePassage(@PathVariable Long passageId) {
    passageService.deletePassage(passageId);

    return ResponseEntity.noContent().build();
  }
}
