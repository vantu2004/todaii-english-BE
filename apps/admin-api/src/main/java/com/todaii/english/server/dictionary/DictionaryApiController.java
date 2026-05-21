package com.todaii.english.server.dictionary;

import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.service.DictionaryService;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.response.DictionaryApiResponse;
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
      @RequestParam(required = false) String word,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "50") @Min(value = 1, message = "Size must be at least 1")
          int size) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

    return ResponseEntity.ok()
        .body(dictionaryService.searchByTodaiiDictionary(currentAdminId, word, page, size));
  }

  @GetMapping("/free-dict")
  public ResponseEntity<DictionaryApiResponse[]> searchByFreeDictionaryApi(
      Authentication authentication, @RequestParam String word) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);
    DictionaryApiResponse[] dictionaryApiResponses =
        dictionaryService.searchByFreeDictionaryApi(currentAdminId, word);

    return ResponseEntity.ok(dictionaryApiResponses);
  }
}
