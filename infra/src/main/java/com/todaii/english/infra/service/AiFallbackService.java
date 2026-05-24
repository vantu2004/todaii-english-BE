package com.todaii.english.infra.service;

import java.util.function.Function;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.enums.UsageType;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AiFallbackService {
  private final ChatClient openAiChatClient;
  private final ChatClient geminiChatClient;
  private final UsageStatisticPort usageStatisticPort;

  public AiFallbackService(
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("geminiChatClient") ChatClient geminiChatClient,
      UsageStatisticPort usageStatisticPort) {
    this.openAiChatClient = openAiChatClient;
    this.geminiChatClient = geminiChatClient;
    this.usageStatisticPort = usageStatisticPort;
  }

  /**
   * Hàm dùng chung để thực thi AI với cơ chế Fallback và tự động Log Usage.
   *
   * @param promptAction: Cục logic chứa prompt (user, system) do Service truyền vào
   */
  public ChatResponse executeWithFallback(
      Long actorId, ActorType actorType, Function<ChatClient, ChatResponse> promptAction) {
    // 1. Thử OpenAI trước
    try {
      /* khi chạy, JVM ném hàm lamda bên call qua đây, apply gán tham số đầu cho hàm lamda là
      openAiChatClient sau đó chạy hàm lamda đó lên và nhận về ChatResponse*/
      ChatResponse response = promptAction.apply(openAiChatClient);
      if (response != null && response.getResult() != null) {
        logUsageStatistic(actorId, actorType, AiProvider.OPENAI, response);

        log.info("RAW RESPONSE = {}", response);

        return response;
      }
    } catch (Exception e) {
      log.warn("❌ OpenAI failed. Falling back to Gemini. Reason: {}", e.getMessage());
    }

    // 2. Nếu OpenAI xịt hoặc trả về null -> Thử Gemini
    try {
      ChatResponse response = promptAction.apply(geminiChatClient);
      if (response != null && response.getResult() != null) {
        logUsageStatistic(actorId, actorType, AiProvider.GEMINI, response);

        log.info("RAW RESPONSE = {}", response);

        return response;
      }
    } catch (Exception e) {
      log.error("💥 Both OpenAI and Gemini failed to process request.", e);
    }

    // 3. Nếu cả 2 đều xịt, ném lỗi ra ngoài cho Service tự xử lý
    throw new BusinessException(500, "All AI providers failed to respond.");
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
          "📊 Saved AI Stat: Provider={}, Model={}, PromptToken:{}, CompletionToken:{},"
              + " TotalTokens: {}",
          aiProvider,
          chatResponse.getMetadata().getModel(),
          usage.getPromptTokens(),
          usage.getCompletionTokens(),
          usage.getTotalTokens());
    }
  }
}
