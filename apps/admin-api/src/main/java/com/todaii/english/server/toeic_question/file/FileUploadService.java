package com.todaii.english.server.toeic_question.file;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final CloudinaryPort cloudinaryPort;
  private final TestRepository testRepository;

  public String upload(Long testId, MultipartFile file) {
    ToeicTest toeicTest =
        testRepository
            .findById(testId)
            .orElseThrow(() -> new BusinessException(404, "Test not found"));

    try {
      return cloudinaryPort.uploadFile(file, "toeic/" + toeicTest.getTitle());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String fileUrl) {
    cloudinaryPort.deleteFile(fileUrl);
  }
}
