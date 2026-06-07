package com.todaii.english.client.dictionary;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.service.DictionaryService;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;
import com.todaii.english.shared.response.TopWordResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dictionary")
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
    Long currentUserId = null;
    if (authentication != null) {
      currentUserId = UserUtils.getCurrentUserId(authentication);
    }

    ActorType actorType = currentUserId == null ? ActorType.GUEST : ActorType.USER;

    return ResponseEntity.ok()
        .body(
            dictionaryService.searchByTodaiiDictionary(currentUserId, actorType, word, page, size));
  }

  @GetMapping("/free-dict")
  public ResponseEntity<DictionaryApiResponse[]> searchByFreeDictionaryApi(
      Authentication authentication,
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    Long currentUserId = null;
    if (authentication != null) {
      currentUserId = UserUtils.getCurrentUserId(authentication);
    }

    ActorType actorType = currentUserId == null ? ActorType.GUEST : ActorType.USER;

    DictionaryApiResponse[] dictionaryApiResponses =
        dictionaryService.searchByFreeDictionaryApi(currentUserId, actorType, word);

    return ResponseEntity.ok(dictionaryApiResponses);
  }

  @GetMapping("/ai-suggestion")
  public ResponseEntity<List<String>> getAiSuggestion(
      Authentication authentication,
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    Long currentUserId = null;
    if (authentication != null) {
      currentUserId = UserUtils.getCurrentUserId(authentication);
    }

    ActorType actorType = currentUserId == null ? ActorType.GUEST : ActorType.USER;

    return ResponseEntity.ok(dictionaryService.getAiSuggestions(word, currentUserId, actorType));
  }

  @GetMapping("/top-words")
  public ResponseEntity<List<TopWordResponse>> getTopWords() {
    return ResponseEntity.ok(dictionaryService.getTopWords());
  }
}
