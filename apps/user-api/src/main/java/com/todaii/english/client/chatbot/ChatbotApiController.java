package com.todaii.english.client.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.shared.enums.AiProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/chatbot")
public class ChatbotApiController {
  private final ChatbotService chatbotService;

  // test stream bằng trình duyệt, postman ko stream đc
  @PostMapping
  public Flux<String> sendMessage(
      @RequestParam @NotBlank(message = "Message must not be blank") String message,
      @RequestParam(defaultValue = "OPENAI") @NotNull(message = "AI Provider is required")
          AiProvider aiProvider) {
    return ResponseEntity.ok().body(chatbotService.sendMessage(message, aiProvider)).getBody();
  }
}
