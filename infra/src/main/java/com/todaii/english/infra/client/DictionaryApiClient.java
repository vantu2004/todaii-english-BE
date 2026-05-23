package com.todaii.english.infra.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.enums.UsageType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DictionaryApiClient implements DictionaryPort {
  private final WebClient webClient;
  private final ChatClient openAiChatClient;
  private final ChatClient geminiChatClient;
  private final UsageStatisticPort usageStatisticPort;
  private final ObjectMapper objectMapper;

  @Value("classpath:/promptTemplates/dictionary/systemPromptAiSuggestionTemplate.st")
  private Resource systemPromptAiSuggestionTemplate;

  @Value("classpath:/promptTemplates/dictionary/userPromptAiSuggestionTemplate.st")
  private Resource userPromptAiSuggestionTemplate;

  public DictionaryApiClient(
      WebClient.Builder builder,
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("geminiChatClient") ChatClient geminiChatClient,
      UsageStatisticPort usageStatisticPort,
      ObjectMapper objectMapper) {
    this.webClient = builder.build();
    this.openAiChatClient = openAiChatClient;
    this.geminiChatClient = geminiChatClient;
    this.usageStatisticPort = usageStatisticPort;
    this.objectMapper = objectMapper;
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
      // 1. Ưu tiên gọi OpenAI
      return executeAiCall(openAiChatClient, word, actorId, actorType, AiProvider.OPENAI);
    } catch (Exception e) {
      log.warn(
          "❌ OpenAI failed for word '{}'. Falling back to Gemini. Reason: {}",
          word,
          e.getMessage());

      try {
        // 2. Fallback sang Gemini nếu OpenAI sập
        return executeAiCall(geminiChatClient, word, actorId, actorType, AiProvider.GEMINI);
      } catch (Exception ex) {
        log.error("💥 Both OpenAI and Gemini failed to suggest for word '{}'", word, ex);
        // Trả về list rỗng thay vì ném lỗi để không làm gãy luồng của User
        return List.of();
      }
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

  private List<String> executeAiCall(
      ChatClient chatClient, String word, Long actorId, ActorType actorType, AiProvider aiProvider)
      throws JsonProcessingException {
    // Thay vì .content(), dùng .chatResponse() để lấy Metadata
    ChatResponse chatResponse =
        chatClient
            .prompt()
            .system(sys -> sys.text(systemPromptAiSuggestionTemplate))
            .user(req -> req.text(userPromptAiSuggestionTemplate).param("input_word", word))
            .call()
            .chatResponse();

    if (chatResponse != null) {
      logUsageStatistic(actorId, actorType, aiProvider, chatResponse);

      String content = chatResponse.getResult().getOutput().getText();
      return objectMapper.readValue(content, new TypeReference<List<String>>() {});
    }

    return List.of();
  }

  private void logUsageStatistic(
      Long actorId, ActorType actorType, AiProvider aiProvider, ChatResponse chatResponse) {
    Usage usage = chatResponse.getMetadata().getUsage();
    if (usage != null) {

      UsageStatistic usageStatistic =
          UsageStatistic.builder()
              .actorId(actorId)
              .actorType(actorType)
              .usageType(UsageType.AI_REQUEST)
              .aiProvider(aiProvider)
              .model(chatResponse.getMetadata().getModel())
              .inputToken(Long.valueOf(usage.getPromptTokens()))
              .outputToken(Long.valueOf(usage.getCompletionTokens()))
              .totalToken(Long.valueOf(usage.getTotalTokens()))
              .build();

      usageStatisticPort.createUsageStatistic(usageStatistic);

      log.info(
          "📊 Saved AI Stat: Provider={}, Model={}, TotalTokens={}",
          aiProvider,
          chatResponse.getMetadata().getModel(),
          usage.getTotalTokens());
    }
  }
}
