package com.todaii.english.client.gg_tranlate;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.shared.request.client.GgTranslateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/gg-translate")
public class GgTranslateApiController {
  private final GgTranslateService ggTranslateService;

  @PostMapping
  public ResponseEntity<List<String>> translate(
      Authentication authentication, @Valid @RequestBody GgTranslateRequest ggTranslateRequest) {
    Long currentUserId = null;
    if (authentication != null) {
      currentUserId = UserUtils.getCurrentUserId(authentication);
    }

    return ResponseEntity.ok(ggTranslateService.translateText(currentUserId, ggTranslateRequest));
  }
}
