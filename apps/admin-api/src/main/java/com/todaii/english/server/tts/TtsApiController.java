package com.todaii.english.server.tts;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/tts")
public class TtsApiController {
  private final TtsService ttsService;

  @GetMapping(produces = "audio/mpeg")
  public ResponseEntity<byte[]> synthesize(
      @RequestParam @NotBlank(message = "Text must not be blank") String text) {
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("audio/mpeg"))
        .header("Content-Disposition", "attachment; filename=\"speech.mp3\"")
        .body(ttsService.call(text));
  }
}
