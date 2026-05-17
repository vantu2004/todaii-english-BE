package com.todaii.english.client.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.todaii.english.shared.enums.AiProvider;

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

    return chatClient
        .prompt()
        .system(promptSystemSpec -> promptSystemSpec.text(systemPromptChatbotTemplate))
        .user(message)
        .call()
        .content();
  }
}
