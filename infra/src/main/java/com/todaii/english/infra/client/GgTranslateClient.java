package com.todaii.english.infra.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.cloud.translate.v3.*;
import com.todaii.english.core.port.GgTranslatePort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = {"google.cloud.project-id", "google.credentials.json"})
public class GgTranslateClient implements GgTranslatePort {
  // inject client đã config bên GgTranslateConfig
  private final TranslationServiceClient translationServiceClient;
  private final String projectId;

  public GgTranslateClient(
      TranslationServiceClient translationServiceClient,
      @Value("${google.cloud.project-id}") String projectId) {
    this.translationServiceClient = translationServiceClient;
    this.projectId = projectId;
  }

  @Override
  public List<String> translateText(
      String sourceLanguage, String targetLanguage, List<String> texts) {
    /* Initialize client that will be used to send requests. This client only needs to be created
    once, and can be reused for multiple requests. After completing all of your requests, call
    the "close" method on the client to safely clean up any remaining background resources.*/
    try {
      // Supported Locations: `global`, [glossary location], or [model location]
      // Glossaries must be hosted in `us-central1`
      // Custom Models must use the same location as your model. (us-central1)
      LocationName parent = LocationName.of(projectId, "global");

      // Supported Mime Types: https://cloud.google.com/translate/docs/supported-formats
      TranslateTextRequest request =
          TranslateTextRequest.newBuilder()
              .setParent(parent.toString())
              .setMimeType("text/plain")
              .setSourceLanguageCode(sourceLanguage)
              .setTargetLanguageCode(targetLanguage)
              .addAllContents(texts)
              .build();

      TranslateTextResponse response = translationServiceClient.translateText(request);

      return response.getTranslationsList().stream().map(Translation::getTranslatedText).toList();
    } catch (Exception e) {
      log.error("Lỗi gọi Google Translate API: ", e);
      throw new RuntimeException("Dịch thuật thất bại: " + e.getMessage());
    }
  }
}
