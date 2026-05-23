package com.todaii.english.client.chatbot;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.enums.UsageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenUsageStreamAdvisor implements StreamAdvisor {
  private final UsageStatisticPort usageStatisticPort;

  @Override
  public Flux<ChatClientResponse> adviseStream(
      ChatClientRequest request, StreamAdvisorChain chain) {
    return chain
        .nextStream(request)

        // đợi stream hoàn tất rồi gom toàn bộ chunk vào List.
        .collectList()

        // convert ngược lại thành Flux
        .flatMapMany(
            responses -> {
              if (responses.isEmpty()) {
                return Flux.empty();
              }

              try {
                Long actorId = (Long) request.context().get("actorId");
                ActorType actorType = (ActorType) request.context().get("actorType");
                AiProvider aiProvider = (AiProvider) request.context().get("aiProvider");

                ChatClientResponse last = responses.getLast();
                ChatResponse chatResponse = last.chatResponse();

                saveUsage(actorId, actorType, aiProvider, chatResponse);
              } catch (Exception e) {
                log.error("Cannot save AI usage statistic", e);
              }

              return Flux.fromIterable(responses);
            });
  }

  private void saveUsage(
      Long actorId, ActorType actorType, AiProvider aiProvider, ChatResponse chatResponse) {
    Usage usage = chatResponse.getMetadata().getUsage();
    if (usage == null) {
      return;
    }

    log.info("USAGE: {}", usage);

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
  }

  @Override
  public String getName() {
    return "TokenUsageStreamAdvisor";
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
