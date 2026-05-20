package com.todaii.english.client.chatbot;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.transaction.Transactional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ChatbotService {
  private final ChatMemory chatMemory;
  private final ChatClient openAiChatClient;
  private final ChatClient geminiChatClient;
  private final TokenUsageStreamAdvisor tokenUsageStreamAdvisor;
  private final ChatMemoryRepository chatMemoryRepository;

  @Value("classpath:/promptTemplates/chatbot/systemPromptChatbotTemplate.st")
  private Resource systemPromptChatbotTemplate;

  public ChatbotService(
      @Qualifier("chatMemory") ChatMemory chatMemory,
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("geminiChatClient") ChatClient geminiChatClient,
      TokenUsageStreamAdvisor tokenUsageStreamAdvisor,
      ChatMemoryRepository chatMemoryRepository) {
    this.chatMemory = chatMemory;
    this.openAiChatClient = openAiChatClient;
    this.geminiChatClient = geminiChatClient;
    this.tokenUsageStreamAdvisor = tokenUsageStreamAdvisor;
    this.chatMemoryRepository = chatMemoryRepository;
  }

  public Page<com.todaii.english.core.entity.ChatMemory> getPagedHistory(
      Long currentUserId, int page, int size) {
    return chatMemoryRepository.findByConversationIdOrderByTimestampDesc(
        currentUserId.toString(), PageRequest.of(page - 1, size));
  }

  public Flux<String> sendMessage(Long currentUserId, String message, AiProvider aiProvider) {
    ChatClient chatClient = aiProvider == AiProvider.GEMINI ? geminiChatClient : openAiChatClient;

    ChatClient.ChatClientRequestSpec prompt =
        chatClient.prompt().system(spec -> spec.text(systemPromptChatbotTemplate)).user(message);

    prompt.advisors(
        a -> {
          if (currentUserId != null) {
            a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build());

            a.param(ChatMemory.CONVERSATION_ID, currentUserId.toString());
          }

          a.advisors(tokenUsageStreamAdvisor);
          a.param("actorId", currentUserId == null ? 0L : currentUserId);
          a.param("actorType", currentUserId == null ? ActorType.GUEST : ActorType.USER);
          a.param("aiProvider", aiProvider);
        });

    Duration firstTokenTimeout =
        aiProvider == AiProvider.GEMINI ? Duration.ofSeconds(10) : Duration.ofSeconds(20);
    Duration streamTimeout =
        aiProvider == AiProvider.GEMINI ? Duration.ofSeconds(60) : Duration.ofSeconds(120);

    AtomicBoolean firstTokenReceived = new AtomicBoolean(false);

    return prompt.stream()
        .content()

        // first token detection
        .doOnNext(
            token -> {
              firstTokenReceived.set(true);
            })

        // timeout strategy
        .timeout(Mono.delay(firstTokenReceived.get() ? streamTimeout : firstTokenTimeout))

        // graceful fallback
        .onErrorResume(
            TimeoutException.class,
            e ->
                Flux.just("AI phản hồi quá chậm. Bạn có thể thử lại hoặc chuyển sang model khác."));
  }

  @Transactional
  public void deleteHistory(Long currentUserId) {
    chatMemoryRepository.deleteAllByConversationId(String.valueOf(currentUserId));
  }
}
