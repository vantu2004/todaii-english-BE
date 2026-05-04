package com.todaii.english.server.toeic_test;

import com.todaii.english.shared.response.ToeicTestResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.request.server.ToeicTestRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toeic/test")
public class TestApiController {
  private final TestService testService;

//  @GetMapping
//  public ResponseEntity<Page<ToeicTestRequest>> getAllPaged(
//      @RequestParam(required = false) Long collectionId, Pageable pageable) {
//    return ResponseEntity.ok(testService.getAllPaged(collectionId, pageable));
//  }

  @GetMapping("/{id}")
  public ResponseEntity<ToeicTestResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(testService.getById(id));
  }

  @PostMapping
  public ResponseEntity<ToeicTestResponse> createTest(
      Authentication authentication, @Valid @RequestBody ToeicTestRequest dto) {
    return ResponseEntity.status(201)
        .body(testService.create(AdminUtils.getCurrentAdminId(authentication), dto));
  }

//  @PutMapping("/{id}")
//  public ResponseEntity<ToeicTest> updateTest(
//      @PathVariable Long id, @RequestBody ToeicTestRequest dto) {
//    return ResponseEntity.ok(testService.update(id, dto));
//  }

//  @DeleteMapping("/{id}")
//  public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
//    testService.delete(id);
//    return ResponseEntity.noContent().build();
//  }
}
