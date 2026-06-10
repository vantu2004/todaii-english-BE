package com.todaii.english.core.port;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryPort {
  String uploadImage(String base64Image, String folderName);

  String uploadFile(MultipartFile multipartFile, String folderName) throws IOException;

  String uploadTtsFile(byte[] audio, String folderName) throws IOException;

  void deleteFile(String fileUrl);
}
