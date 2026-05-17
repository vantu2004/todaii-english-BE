package com.todaii.english.client.chatbot;

import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.todaii.english.shared.enums.AiProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatbotService {
  private final ChatClient openAiChatClient;
  private final ChatClient geminiChatClient;

  @Value("classpath:/promptTemplates/chatbot/systemPromptChatbotTemplate.st")
  private Resource systemPromptChatbotTemplate;

  public ChatbotService(
      @Qualifier("openAiChatClient") ChatClient openAiChatClient,
      @Qualifier("geminiChatClient") ChatClient geminiChatClient) {
    this.openAiChatClient = openAiChatClient;
    this.geminiChatClient = geminiChatClient;
  }

  public String sendMessage(String message, AiProvider provider) {
    ChatClient chatClient = provider == AiProvider.GEMINI ? geminiChatClient : openAiChatClient;

    ChatResponse chatResponse =
        chatClient
            .prompt()
            .system(spec -> spec.text(systemPromptChatbotTemplate))
            .user(message)
            .call()
            .chatResponse();

    log.info("{}", chatResponse);

    return Objects.requireNonNull(chatResponse).getResult().getOutput().getText();
  }
}
