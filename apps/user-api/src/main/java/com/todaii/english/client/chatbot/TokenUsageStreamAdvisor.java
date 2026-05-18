package com.todaii.english.client.chatbot;

import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.todaii.english.client.usage_statistic.UsageStatisticRepository;
import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.enums.UsageType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class TokenUsageStreamAdvisor implements StreamAdvisor {
  private final UsageStatisticRepository usageStatisticRepository;
  private final String openAiModel;
  private final String geminiModel;

  public TokenUsageStreamAdvisor(
      UsageStatisticRepository usageStatisticRepository,
      @Value("${spring.ai.openai.chat.options.model}") String openAiModel,
      @Value("${spring.ai.google.genai.chat.options.model}") String geminiModel) {
    this.usageStatisticRepository = usageStatisticRepository;
    this.openAiModel = openAiModel;
    this.geminiModel = geminiModel;
  }

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

                ChatClientResponse last = responses.get(responses.size() - 1);

                Usage usage = last.chatResponse().getMetadata().getUsage();

                saveUsage(actorId, actorType, aiProvider, usage);
              } catch (Exception e) {
                log.error("Cannot save AI usage statistic", e);
              }

              return Flux.fromIterable(responses);
            });
  }

  private void saveUsage(Long actorId, ActorType actorType, AiProvider aiProvider, Usage usage) {
    if (usage == null) {
      return;
    }

    log.info("USAGE: {}", usage);

    LocalDate currentDate = LocalDate.now();
    String model = aiProvider == AiProvider.OPENAI ? openAiModel : geminiModel;

    UsageStatistic usageStatistic =
        usageStatisticRepository.findByActorIdAndActorTypeAndUsageTypeAndModelAndCreatedAt(
            actorId, actorType, UsageType.AI_REQUEST, model, currentDate);

    Long inputToken = usage.getPromptTokens() == null ? 0L : usage.getPromptTokens().longValue();
    Long outputToken =
        usage.getCompletionTokens() == null ? 0L : usage.getCompletionTokens().longValue();
    Long totalToken = usage.getTotalTokens() == null ? 0L : usage.getTotalTokens().longValue();

    if (usageStatistic == null) {
      usageStatistic =
          UsageStatistic.builder()
              .actorId(actorId)
              .actorType(actorType)
              .usageType(UsageType.AI_REQUEST)
              .build();
    }

    usageStatistic.setQuantity(usageStatistic.getQuantity() + 1);
    usageStatistic.setInputToken(usageStatistic.getInputToken() + inputToken);
    usageStatistic.setOutputToken(usageStatistic.getOutputToken() + outputToken);
    usageStatistic.setTotalToken(usageStatistic.getTotalToken() + totalToken);
    usageStatistic.setAiProvider(aiProvider);
    usageStatistic.setModel(model);

    usageStatisticRepository.save(usageStatistic);
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
