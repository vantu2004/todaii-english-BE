package com.todaii.english.client.toeic_tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.toeic.ToeicTag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/toeic/tag")
@RequiredArgsConstructor
public class TagApiController {
  private final TagService tagService;

  @GetMapping
  public ResponseEntity<List<ToeicTag>> getAllTags() {
    return ResponseEntity.ok().body(tagService.getAllTags());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ToeicTag> getById(@PathVariable Long id) {
    return ResponseEntity.ok().body(tagService.findById(id));
  }
}
