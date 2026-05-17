package com.todaii.english.client.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.enums.AiProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/chatbot")
public class ChatbotApiController {
  private final ChatbotService chatbotService;

  @PostMapping
  public ResponseEntity<String> sendMessage(
      @RequestParam @NotBlank(message = "Message must not be blank") String message,
      @RequestParam @NotNull(message = "AI Provider is required") AiProvider aiProvider) {
    return ResponseEntity.ok().body(chatbotService.sendMessage(message, aiProvider));
  }
}
