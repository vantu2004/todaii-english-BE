package com.todaii.english.server.toeic_question;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {
  private final FileUploadService fileStorageService;

  @PostMapping(value = "/toeic/test/{testId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> upload(
      @PathVariable Long testId, @RequestParam MultipartFile file) {
    return ResponseEntity.ok(fileStorageService.upload(testId, file));
  }
}
