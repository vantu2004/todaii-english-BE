package com.todaii.english.client.toeic_tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/toeic/test/{testId}/tag")
@RequiredArgsConstructor
public class TagApiController {
  private final TagService tagService;

  @GetMapping
  public ResponseEntity<List<ToeicTag>> findAllTagsByTestId(@PathVariable Long testId) {
    return ResponseEntity.ok().body(tagService.findAllTagsByTestId(testId));
  }

  @GetMapping("/{tagId}")
  public ResponseEntity<List<ToeicQuestion>> findQuestionsByTag(
      @PathVariable Long testId, @PathVariable Long tagId) {
    return ResponseEntity.ok().body(tagService.findQuestionsByTag(testId, tagId));
  }
}
