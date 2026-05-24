package com.todaii.english.client.toeic_collection;

import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.toeic.ToeicCollection;
import com.todaii.english.shared.response.PagedResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/collection")
public class CollectionApiController {
  private final CollectionService collectionService;

  @GetMapping
  public ResponseEntity<PagedResponse<ToeicCollection>> getAllPaged(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) String keyword) {

    Page<ToeicCollection> collections =
        collectionService.findAllPaged(page, size, sortBy, direction, keyword);

    PagedResponse<ToeicCollection> response =
        new PagedResponse<>(
            collections.getContent(),
            page,
            size,
            collections.getTotalElements(),
            collections.getTotalPages(),
            collections.isFirst(),
            collections.isLast(),
            sortBy,
            direction);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{collectionId}")
  public ResponseEntity<ToeicCollection> getCollectionById(@PathVariable Long collectionId) {
    return ResponseEntity.ok(collectionService.getById(collectionId));
  }
}
