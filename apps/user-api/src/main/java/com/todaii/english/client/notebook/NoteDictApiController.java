package com.todaii.english.client.notebook;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.entity.DictionaryWord;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/notebook")
public class NoteDictApiController {
  private final NoteDictService noteDictService;

  @GetMapping("/{noteId}/words")
  public ResponseEntity<List<DictionaryWord>> getWords(
      Authentication authentication, @PathVariable Long noteId) {
    return ResponseEntity.ok(
        noteDictService.getEntries(UserUtils.getCurrentUserId(authentication), noteId));
  }

  @PutMapping("/{noteId}/word")
  public ResponseEntity<Void> addWord(
      Authentication authentication,
      @PathVariable Long noteId,
      @RequestParam @NotBlank(message = "Word must not be blank") String word) {
    noteDictService.addEntry(UserUtils.getCurrentUserId(authentication), noteId, word);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{noteId}/word/{entryId}")
  public ResponseEntity<Void> removeWord(
      Authentication authentication, @PathVariable Long noteId, @PathVariable Long entryId) {
    noteDictService.removeEntry(UserUtils.getCurrentUserId(authentication), noteId, entryId);
    return ResponseEntity.ok().build();
  }
}
