package com.todaii.english.client.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.todaii.english.shared.enums.AiProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class ChatbotService {
  private final ChatMemory chatMemory;
  private final ChatClient openAiChatClient;
  private final ChatClient geminiChatClient;

  @Value("classpath:/promptTemplates/chatbot/systemPromptChatbotTemplate.st")
  private Resource systemPromptChatbotTemplate;

  public ChatbotService(
      @Qualifier("chatMemory") ChatMemory chatMemory,
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("geminiChatClient") ChatClient geminiChatClient) {
    this.chatMemory = chatMemory;
    this.openAiChatClient = openAiChatClient;
    this.geminiChatClient = geminiChatClient;
  }

  public Flux<String> sendMessage(Long currentUserId, String message, AiProvider provider) {
    ChatClient chatClient = provider == AiProvider.GEMINI ? geminiChatClient : openAiChatClient;

    ChatClient.ChatClientRequestSpec prompt =
        chatClient.prompt().system(spec -> spec.text(systemPromptChatbotTemplate)).user(message);

    // chỉ bật memory khi có user id
    if (currentUserId != null) {
      prompt.advisors(
          a ->
              a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                  .param(ChatMemory.CONVERSATION_ID, currentUserId.toString()));
    }

    return prompt.stream().content();
  }
}
