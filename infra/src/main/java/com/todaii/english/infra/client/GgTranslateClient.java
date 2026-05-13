package com.todaii.english.infra.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.translate.v3.*;
import com.google.protobuf.ByteString;
import com.todaii.english.core.port.GgTranslatePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GgTranslateClient implements GgTranslatePort {
  // inject client đã config bên GgTranslateConfig
  private final TranslationServiceClient translationServiceClient;

  @Value("${google.cloud.project-id}")
  private String projectId;

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
      log.error("Translate texts failed: ", e);
      throw new RuntimeException("Translate texts failed: " + e.getMessage());
    }
  }

  @Override
  public byte[] translateDocument(
      String sourceLanguage, String targetLanguage, MultipartFile file) {
    try {
      byte[] fileBytes = file.getBytes();

      ByteString content = ByteString.copyFrom(fileBytes);

      DocumentInputConfig documentInputConfig =
          DocumentInputConfig.newBuilder()
              .setMimeType(file.getContentType())
              .setContent(content)
              .build();

      TranslateDocumentRequest.Builder requestBuilder =
          TranslateDocumentRequest.newBuilder()
              .setParent("projects/" + projectId + "/locations/global")
              .setTargetLanguageCode(targetLanguage)
              .setDocumentInputConfig(documentInputConfig);

      // optional
      if (sourceLanguage != null && !sourceLanguage.isBlank()) {
        requestBuilder.setSourceLanguageCode(sourceLanguage);
      }

      TranslateDocumentResponse response =
          translationServiceClient.translateDocument(requestBuilder.build());

      return response.getDocumentTranslation().getByteStreamOutputs(0).toByteArray();

    } catch (Exception e) {
      log.error("Translate document failed: ", e);
      throw new RuntimeException("Translate document failed: " + e.getMessage());
    }
  }
}
