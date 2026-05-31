package com.todaii.english.infra.client;

import java.util.List;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.port.VocabExtractionPort;
import com.todaii.english.infra.service.AiFallbackService;
import com.todaii.english.shared.enums.ActorType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VocabExtractionClient implements VocabExtractionPort {
  private final AiFallbackService aiFallbackService;
  private final ObjectMapper objectMapper;

  @Value("classpath:/promptTemplates/systemPromptVocabExtractionTemplate.st")
  private Resource systemPromptVocabExtractionTemplate;

  @Override
  public List<String> vocabExtraction(String text, Long actorId, ActorType actorType) {
    try {
      // Truyền logic prompt dưới dạng Lambda Function để bên kia tự gán Chatclient cho chạy
      ChatResponse response =
          aiFallbackService.executeWithFallback(
              actorId,
              actorType,
              client ->
                  client
                      .prompt()
                      .system(sys -> sys.text(systemPromptVocabExtractionTemplate))
                      .user(text)
                      .call()
                      .chatResponse());

      String content = response.getResult().getOutput().getText();

      return objectMapper.readValue(content, new TypeReference<>() {});
    } catch (Exception ex) {
      // Business logic của hàm này là: Nếu lỗi AI thì ko ném lỗi ra ngoài, mà trả về List rỗng
      log.error("💥 All AI providers failed to extract vocabulary from text '{}'", text, ex);
      return List.of();
    }
  }
}
