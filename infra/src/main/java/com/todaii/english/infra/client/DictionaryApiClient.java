package com.todaii.english.infra.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.infra.service.AiFallbackService;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DictionaryApiClient implements DictionaryPort {
  private final WebClient webClient;
  private final ObjectMapper objectMapper;
  private final AiFallbackService aiFallbackService;

  @Value("classpath:/promptTemplates/dictionary/systemPromptAiSuggestionTemplate.st")
  private Resource systemPromptAiSuggestionTemplate;

  @Value("classpath:/promptTemplates/dictionary/userPromptAiSuggestionTemplate.st")
  private Resource userPromptAiSuggestionTemplate;

  public DictionaryApiClient(
      WebClient.Builder builder, ObjectMapper objectMapper, AiFallbackService aiFallbackService) {
    this.webClient = builder.build();
    this.objectMapper = objectMapper;
    this.aiFallbackService = aiFallbackService;
  }

  @Override
  public DictionaryApiResponse[] lookupFreeDictionaryApi(String word) {
    String url = ApiUrl.DICTIONARY_BASE_URL + "/" + encode(word);
    return get(url, DictionaryApiResponse[].class);
  }

  @Override
  public TodaiiEnglishResponse lookupTodaiiDictionaryApi(String word, int page, int size) {
    String url = buildTodaiiUrl(word, page, size);
    return get(url, TodaiiEnglishResponse.class);
  }

  @Override
  public List<String> getAiSuggestions(String word, Long actorId, ActorType actorType) {
    try {
      // Truyền logic prompt dưới dạng Lambda Function để bên kia tự gán Chatclient cho chạy
      ChatResponse response =
          aiFallbackService.executeWithFallback(
              actorId,
              actorType,
              client ->
                  client
                      .prompt()
                      .system(
                          promptSystemSpec ->
                              promptSystemSpec.text(systemPromptAiSuggestionTemplate))
                      .user(
                          promptUserSpec ->
                              promptUserSpec
                                  .text(userPromptAiSuggestionTemplate)
                                  .param("input_word", word))
                      .call()
                      .chatResponse());

      String content = response.getResult().getOutput().getText();

      return objectMapper.readValue(content, new TypeReference<>() {});
    } catch (Exception ex) {
      // Business logic của hàm này là: Nếu lỗi AI thì ko ném lỗi ra ngoài, mà trả về List rỗng
      log.error("💥 All AI providers failed to suggest for word '{}'", word, ex);
      return List.of();
    }
  }

  // <T> báo là trong hàm này sẽ có sử dụng generic type
  private <T> T get(String url, Class<T> responseType) {
    try {
      return webClient
          .get()
          .uri(url)
          .retrieve()

          // 4xx
          .onStatus(
              HttpStatusCode::is4xxClientError,
              res ->
                  res.bodyToMono(String.class)
                      .doOnNext(body -> log.warn("❌ 4xx error: url={} body={}", url, body))
                      .flatMap(body -> Mono.error(new BusinessException(404, "Word not found"))))

          // 5xx
          .onStatus(
              HttpStatusCode::is5xxServerError,
              res ->
                  res.bodyToMono(String.class)
                      .doOnNext(body -> log.error("💥 5xx error: url={} body={}", url, body))
                      .flatMap(body -> Mono.error(new BusinessException(500, body))))
          .bodyToMono(responseType)
          .block();

    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error when calling API: url={} msg={}", url, e.getMessage(), e);
      throw new BusinessException(500, "External API error");
    }
  }

  private String buildTodaiiUrl(String word, int page, int size) {
    return String.format(ApiUrl.TODAII_DICT_BASE_URL, encode(word), page, size);
  }

  private String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
