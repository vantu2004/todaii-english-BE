package com.todaii.english.server.toeic_tag;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.toeic.ToeicTag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/toeic/tag")
@Validated
@RequiredArgsConstructor
public class TagApiController {
  private final TagService tagService;

  @GetMapping
  public ResponseEntity<List<ToeicTag>> getAllTags() {
    return ResponseEntity.ok().body(tagService.getAllTags());
  }

  @GetMapping("/{id}")
  public ToeicTag getById(@PathVariable Long id) {
    return tagService.findById(id);
  }

  @PostMapping
  public ResponseEntity<ToeicTag> create(
      @NotBlank(message = "Toeic tag name cannot be blank")
          @Length(max = 191, message = "Toeic tag name must not exceed 191 characters")
          @RequestParam
          String name,
      @Range(min = 1, max = 7, message = "Part number must be between 1 and 7")
          @RequestParam(required = false)
          Integer partNumber) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(name, partNumber));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ToeicTag> update(
      @PathVariable Long id,
      @NotBlank(message = "Toeic tag name cannot be blank")
          @Length(max = 191, message = "Toeic tag name must not exceed 191 characters")
          @RequestParam
          String name,
      @Range(min = 1, max = 7, message = "Part number must be between 1 and 7")
          @RequestParam(required = false)
          Integer partNumber) {
    return ResponseEntity.ok().body(tagService.update(id, name, partNumber));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    tagService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
