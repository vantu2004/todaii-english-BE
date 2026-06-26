package com.todaii.english.client.toeic_test;

import java.util.List;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.RecommendationService;
import com.todaii.english.shared.dto.learning.TestRecommendationDTO;
import com.todaii.english.shared.dto.toeic.ToeicTestDTO;
import com.todaii.english.shared.response.PagedResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/test")
public class TestApiController {
  private final TestService testService;
  private final RecommendationService recommendationService;

  // gợi ý tests dựa trên điểm yếu của user
  @GetMapping("/recommend")
  public ResponseEntity<TestRecommendationDTO> recommendTests(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(recommendationService.recommendTests(userId));
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ToeicTestDTO>> getAllTestsPaged(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String keyword) {
    Page<ToeicTestDTO> testResponses =
        testService.getAllPublishedPaged(page, size, sortBy, direction, keyword);

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

  @GetMapping("/collection/{collectionId}")
  public ResponseEntity<PagedResponse<ToeicTestDTO>> getAllTestsByCollectionPaged(
      @PathVariable Long collectionId,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String keyword) {
    Page<ToeicTestDTO> testResponses =
        testService.findPublishedByCollectionId(
            collectionId, page, size, sortBy, direction, keyword);

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
  public ResponseEntity<ToeicTestDTO> getTestById(@PathVariable Long id) {
    return ResponseEntity.ok(testService.getPublishedTestDTOById(id));
  }

  // lấy danh sách tests đc lưu bởi user
  @GetMapping("/saved")
  public ResponseEntity<List<ToeicTestDTO>> getSavedTestsByUserId(Authentication authentication) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(testService.getSavedTestsByUserId(currentUserId));
  }

  // check test có đc lưu bởi user ko
  @GetMapping("/{testId}/is-saved")
  public ResponseEntity<Boolean> isSavedByUser(
      Authentication authentication, @PathVariable Long testId) {
    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(testService.isSavedByUser(testId, currentUserId));
  }
}
