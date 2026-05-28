package com.todaii.english.server.file;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final CloudinaryPort cloudinaryPort;
  private final TestRepository testRepository;
  private final UsageStatisticPort usageStatisticPort;

  public String upload(Long currentAdminId, Long testId, MultipartFile file) {
    String folderName;
    if (testId == null || testId == 0L) {
      folderName = "toeic/temp";
    } else {
      ToeicTest toeicTest =
          testRepository
              .findById(testId)
              .orElseThrow(() -> new BusinessException(404, "Test not found"));
      folderName = "toeic/" + toeicTest.getTitle();
    }

    usageStatisticPort.createUsageStatistic(
        usageStatisticPort.createCloudinaryStatistic(currentAdminId, ActorType.ADMIN));

    try {
      return cloudinaryPort.uploadFile(file, folderName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String fileUrl) {
    cloudinaryPort.deleteFile(fileUrl);
  }
}
