package com.todaii.english.core.port;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryPort {
  String uploadImage(String base64Image, String folderName);

  String uploadFile(MultipartFile multipartFile, String foldername) throws IOException;

  void deleteFile(String fileUrl);
}
