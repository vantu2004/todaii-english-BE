package com.todaii.english.client.toeic_collection;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.toeic.ToeicCollection;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/collection")
public class CollectionApiController {
  private final CollectionService collectionService;

  @GetMapping
  public ResponseEntity<List<ToeicCollection>> getAllCollections() {
    return ResponseEntity.ok(collectionService.getAllCollections());
  }
}
