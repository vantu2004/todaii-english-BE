package com.todaii.english.infra.client;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.todaii.english.core.port.CloudinaryPort;

@Component
public class CloudinaryClient implements CloudinaryPort {
  private final Cloudinary cloudinary;

  // Regex kiểm tra URL Cloudinary cơ bản
  private static final String CLOUDINARY_URL_PATTERN =
      "^https?://res\\.cloudinary\\.com/[\\w-]+/(image|video|raw)/upload/.*$";

  public CloudinaryClient(
      @Value("${cloudinary.cloud-name}") String cloudinaryCloudName,
      @Value("${cloudinary.api-key}") String cloudinaryApiKey,
      @Value("${cloudinary.api-secret}") String cloudinaryApiSecret) {

    this.cloudinary =
        new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name",
                cloudinaryCloudName,
                "api_key",
                cloudinaryApiKey,
                "api_secret",
                cloudinaryApiSecret));
  }

  @Override
  public String uploadImage(String base64Image, String folderName) {
    return upload(base64Image, folderName, "image");
  }

  @Override
  public String uploadFile(MultipartFile multipartFile, String folderName) throws IOException {
    return upload(multipartFile.getBytes(), folderName, "auto");
  }

  @Override
  public String uploadTtsFile(byte[] audio, String folderName) throws IOException {
    return upload(audio, folderName, "video");
  }

  @Override
  public void deleteFile(String fileUrl) {
    try {
      if (fileUrl == null || !Pattern.matches(CLOUDINARY_URL_PATTERN, fileUrl)) {
        return;
      }

      String publicId = extractPublicId(fileUrl);
      String resourceType = resolveResourceType(fileUrl);
      cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete file", e);
    }
  }

  private String upload(Object file, String folderName, String resourceType) {
    try {
      Map<?, ?> uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file,
                  ObjectUtils.asMap(
                      "folder",
                      "todaii/" + folderName,
                      "resource_type",
                      resourceType,
                      "use_filename",
                      false,
                      "unique_filename",
                      true));

      return uploadResult.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload file to Cloudinary", e);
    }
  }

  private String extractPublicId(String fileUrl) {
    String[] parts = fileUrl.split("/upload/");

    String path = parts[1];
    path = path.replaceFirst("v\\d+/", "");

    // tìm ko thấy thì trả về -1
    int extensionIndex = path.lastIndexOf(".");
    if (extensionIndex != -1) {
      path = path.substring(0, extensionIndex);
    }

    return path;
  }

  private String resolveResourceType(String fileUrl) {
    String lower = fileUrl.toLowerCase();
    if (lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".mp4")) {
      return "video";
    }

    return "image";
  }
}
