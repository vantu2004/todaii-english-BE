package com.todaii.english.server.file;

import com.todaii.english.server.AdminUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cloudinary/file")
public class FileUploadController {
  private final FileUploadService fileStorageService;

  @PostMapping(value = "/toeic/test/{testId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> upload(Authentication authentication,
                                       @PathVariable Long testId, @RequestParam MultipartFile file) {
    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);
    return ResponseEntity.ok(fileStorageService.upload(currentAdminId, testId, file));
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(@RequestParam String fileUrl) {
    fileStorageService.delete(fileUrl);

    return ResponseEntity.noContent().build();
  }
}
