package com.todaii.english.infra.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;

@Configuration
public class GgTranslateConfig {
  // lấy file cấu hình dựa theo classpath (classpath nghĩa là tìm file trong thư mục resources)
  @Value("classpath:google-credentials.json")
  private Resource credentialsResource;

  @Bean
  public TranslationServiceClient translationServiceClient() throws IOException {
    // Đọc file JSON
    GoogleCredentials credentials =
        GoogleCredentials.fromStream(credentialsResource.getInputStream());

    // Cấu hình Client với thông tin xác thực
    TranslationServiceSettings settings =
        TranslationServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();

    // Trả về đối tượng Client
    return TranslationServiceClient.create(settings);
  }
}
