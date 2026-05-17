package com.todaii.english.infra.config;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.todaii.english.infra.advisors.TokenUsageAuditAdvisor;

@Configuration
public class ChatClientConfig {
  @Bean(name = "chatMemory")
  public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .maxMessages(10)
        .chatMemoryRepository(jdbcChatMemoryRepository)
        .build();
  }

  @Bean(name = "openAiChatClient")
  public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
    return buildChatClient(openAiChatModel);
  }

  @Bean(name = "geminiChatClient")
  public ChatClient geminiChatClient(GoogleGenAiChatModel googleGenAiChatModel) {
    return buildChatClient(googleGenAiChatModel);
  }

  private ChatClient buildChatClient(ChatModel chatModel) {
    String defaultSystemMessage = "You are an English teacher with many years of experience.";
    String defaultUserMessage = "How can you help me?";

    return ChatClient.builder(chatModel)
        .defaultSystem(defaultSystemMessage)
        .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()))
        .defaultUser(defaultUserMessage)
        .build();
  }
}
