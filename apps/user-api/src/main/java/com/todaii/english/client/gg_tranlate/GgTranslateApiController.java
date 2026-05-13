package com.todaii.english.client.gg_tranlate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.port.GgTranslatePort;
import com.todaii.english.shared.request.client.gg_translate.GgTranslateDocRequest;
import com.todaii.english.shared.request.client.gg_translate.GgTranslateTextRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/gg-translate")
public class GgTranslateApiController {
  private final GgTranslatePort ggTranslatePort;

  @PostMapping("/text")
  public ResponseEntity<List<String>> translateText(
      @Valid @RequestBody GgTranslateTextRequest request) {
    return ResponseEntity.ok(
        ggTranslatePort.translateText(
            request.getSourceLanguage(), request.getTargetLanguage(), request.getTexts()));
  }

  @PostMapping(path = "/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<byte[]> translateDocument(
      @Valid @ModelAttribute GgTranslateDocRequest request) {
    byte[] translatedFile =
        ggTranslatePort.translateDocument(
            request.getSourceLanguage(), request.getTargetLanguage(), request.getMultipartFile());

    String filename = "translated-" + request.getMultipartFile().getOriginalFilename();

    ContentDisposition contentDisposition =
        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(translatedFile);
  }
}
