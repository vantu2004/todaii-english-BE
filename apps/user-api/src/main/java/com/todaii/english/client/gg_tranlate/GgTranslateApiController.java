package com.todaii.english.client.gg_tranlate;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.port.GgTranslatePort;
import com.todaii.english.shared.request.client.GgTranslateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/gg-translation")
public class GgTranslateApiController {
  private final GgTranslatePort ggTranslationPort;

  @PostMapping
  public ResponseEntity<List<String>> translate(@RequestBody GgTranslateRequest request) {
    return ResponseEntity.ok(
        ggTranslationPort.translateText(request.targetLanguage(), request.texts()));
  }
}
