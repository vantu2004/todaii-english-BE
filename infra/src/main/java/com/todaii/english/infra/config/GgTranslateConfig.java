package com.todaii.english.infra.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;

@Configuration
@ConditionalOnProperty(name = {"google.cloud.project-id", "google.credentials.json"})
public class GgTranslateConfig {

  // Lấy JSON credentials từ biến môi trường trên Render
  @Value("${google.credentials.json}")
  private String credentialsJson;

  @Bean
  public TranslationServiceClient translationServiceClient() throws IOException {

    // Kiểm tra ENV có tồn tại không
    if (credentialsJson == null || credentialsJson.isBlank()) {
      throw new RuntimeException("Env variavle GOOGLE_CREDENTIALS_JSON missing");
    }

    // Fix lỗi Render escape ký tự xuống dòng (\n bị thành \\n)
    String fixedJson = credentialsJson.replace("\\n", "\n");

    // Chuyển JSON → InputStream
    GoogleCredentials credentials =
        GoogleCredentials.fromStream(
            new ByteArrayInputStream(fixedJson.getBytes(StandardCharsets.UTF_8)));

    // Cấu hình Google Translate client
    TranslationServiceSettings settings =
        TranslationServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();

    return TranslationServiceClient.create(settings);
  }
}
