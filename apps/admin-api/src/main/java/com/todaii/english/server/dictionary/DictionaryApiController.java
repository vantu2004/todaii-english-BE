package com.todaii.english.server.dictionary;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.service.DictionaryService;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.PagedResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v2/dictionary")
public class DictionaryApiController {
  private final DictionaryService dictionaryService;

  @GetMapping("/todaii-dict")
  public ResponseEntity<TodaiiEnglishResponse> searchByTodaiiDictionary(
      Authentication authentication,
      @RequestParam @NotBlank(message = "Word must not be blank") String word,
      @RequestParam(defaultValue = "1")
          @Min(value = 1, message = "Page must be greater than or equal to 1")
          int page,
      @RequestParam(defaultValue = "50")
          @Min(value = 1, message = "Size must be greater than or equal to 1")
          int size) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

    return ResponseEntity.ok()
        .body(dictionaryService.searchByTodaiiDictionary(currentAdminId, word, page, size));
  }

  @GetMapping("/free-dict")
  public ResponseEntity<DictionaryApiResponse[]> searchByFreeDictionaryApi(
      Authentication authentication,
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);
    DictionaryApiResponse[] dictionaryApiResponses =
        dictionaryService.searchByFreeDictionaryApi(currentAdminId, word);

    return ResponseEntity.ok(dictionaryApiResponses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DictionaryWord> getWordById(@PathVariable Long id) {
    return ResponseEntity.ok(dictionaryService.getWordById(id));
  }

  @GetMapping("/search")
  public ResponseEntity<PagedResponse<DictionaryWord>> searchInDb(
      @RequestParam @NotBlank(message = "Word must not be blank") String word,
      @RequestParam(defaultValue = "1")
          @Min(value = 1, message = "Page must be greater than or equal to 1")
          int page,
      @RequestParam(defaultValue = "50")
          @Min(value = 1, message = "Size must be greater than or equal to 1")
          int size) {
    Page<DictionaryWord> dictionaryWords = dictionaryService.searchInDb(word, page, size);

    PagedResponse<DictionaryWord> response =
        new PagedResponse<>(
            dictionaryWords.getContent(),
            page,
            size,
            dictionaryWords.getTotalElements(),
            dictionaryWords.getTotalPages(),
            dictionaryWords.isFirst(),
            dictionaryWords.isLast(),
            "word",
            "asc");

    return ResponseEntity.ok(response);
  }

  // Phân trang kiểu Infinite Scroll (Cuộn vô tận)
  @GetMapping("/cursor")
  public ResponseEntity<List<DictionaryWord>> getAllWordsCursor(
      @RequestParam(defaultValue = "0") Long lastId,
      @RequestParam(defaultValue = "50")
          @Min(value = 1, message = "Size must be greater than or equal to 1")
          int size) {
    return ResponseEntity.ok(dictionaryService.getAllWordsByCursor(lastId, size));
  }

  @GetMapping("/ai-suggestion")
  public ResponseEntity<List<String>> getAiSuggestion(
      Authentication authentication,
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

    return ResponseEntity.ok(dictionaryService.getAiSuggestions(word, currentAdminId));
  }

  @PostMapping
  public ResponseEntity<DictionaryWord> createWord(
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    DictionaryWord created = dictionaryService.createWord(word);

    return ResponseEntity.status(201).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<DictionaryWord> updateWord(
      @PathVariable Long id,
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    return ResponseEntity.ok(dictionaryService.updateWord(id, word));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
    dictionaryService.deleteWord(id);

    return ResponseEntity.noContent().build();
  }
}
