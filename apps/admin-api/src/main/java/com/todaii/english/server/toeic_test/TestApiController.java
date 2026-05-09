package com.todaii.english.server.toeic_test;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.dto.ToeicTestDTO;
import com.todaii.english.shared.request.server.toeic.ToeicTestRequest;
import com.todaii.english.shared.response.PagedResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/test")
public class TestApiController {
  private final TestService testService;

  @GetMapping
  public ResponseEntity<PagedResponse<ToeicTestDTO>> getAllPaged(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String keyword) {

    Page<ToeicTestDTO> testResponses =
        testService.getAllPaged(page, size, sortBy, direction, keyword);

    PagedResponse<ToeicTestDTO> response =
        new PagedResponse<>(
            testResponses.getContent(),
            page,
            size,
            testResponses.getTotalElements(),
            testResponses.getTotalPages(),
            testResponses.isFirst(),
            testResponses.isLast(),
            sortBy,
            direction);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ToeicTestDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(testService.getById(id));
  }

  @PostMapping
  public ResponseEntity<ToeicTestDTO> createTest(@Valid @RequestBody ToeicTestRequest dto) {
    return ResponseEntity.status(201).body(testService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ToeicTestDTO> updateTest(
      @PathVariable Long id, @RequestBody ToeicTestRequest dto) {
    return ResponseEntity.ok(testService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
    testService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
