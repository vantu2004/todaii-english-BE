package com.todaii.english.server.toeic_passage;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/test/{testId}/part/{partNumber}/passage")
public class PassageApiController {
  private final PassageService passageService;

  //  @GetMapping
  //  public ResponseEntity<Page<ToeicPassageDTO>> getAllPaged(
  //      @RequestParam(required = false) Long testId, Pageable pageable) {
  //    return ResponseEntity.ok(passageService.getAllPaged(testId, pageable));
  //  }
  //
  //  @GetMapping("/{id}")
  //  public ResponseEntity<ToeicPassageDTO> getById(@PathVariable Long id) {
  //    ToeicPassageDTO entity = passageService.getById(id);
  //    return ResponseEntity.ok(entity);
  //  }
  //
  //  @PostMapping
  //  public ResponseEntity<ToeicPassage> createPassage(@Valid @RequestBody ToeicPassageDTO dto) {
  //    return ResponseEntity.status(HttpStatus.CREATED).body(passageService.create(dto));
  //  }
  //
  //  @PutMapping("/{id}")
  //  public ResponseEntity<ToeicPassage> updatePassage(
  //      @PathVariable Long id, @Valid @RequestBody ToeicPassageDTO dto) {
  //    return ResponseEntity.ok(passageService.update(id, dto));
  //  }
  //
  //  @DeleteMapping("/{id}")
  //  public ResponseEntity<Void> deletePassage(@PathVariable Long id) {
  //    passageService.delete(id);
  //    return ResponseEntity.noContent().build();
  //  }
}
