package com.todaii.english.client.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class ChatbotService {
  private final ChatMemory chatMemory;
  private final ChatClient openAiChatClient;
  private final ChatClient geminiChatClient;
  private final TokenUsageStreamAdvisor tokenUsageStreamAdvisor;

  @Value("classpath:/promptTemplates/chatbot/systemPromptChatbotTemplate.st")
  private Resource systemPromptChatbotTemplate;

  public ChatbotService(
      @Qualifier("chatMemory") ChatMemory chatMemory,
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("geminiChatClient") ChatClient geminiChatClient,
      TokenUsageStreamAdvisor tokenUsageStreamAdvisor) {
    this.chatMemory = chatMemory;
    this.openAiChatClient = openAiChatClient;
    this.geminiChatClient = geminiChatClient;
    this.tokenUsageStreamAdvisor = tokenUsageStreamAdvisor;
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
          // dùng bên TokenUsageStreamAdvisor để tính và lưu token
          a.param("actorId", currentUserId == null ? 0L : currentUserId);
          a.param("actorType", currentUserId == null ? ActorType.GUEST : ActorType.USER);
          a.param("aiProvider", aiProvider);
        });

    return prompt.stream().content();
  }
}
