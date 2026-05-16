package com.todaii.english.infra.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
  @Bean
  public ChatClient openAiChatClient(OpenAiChatModel model) {
    return ChatClient.builder(model).build();
  }

  @Bean
  public ChatClient geminiChatClient(GoogleGenAiChatModel model) {
    return ChatClient.builder(model).build();
  }
}
