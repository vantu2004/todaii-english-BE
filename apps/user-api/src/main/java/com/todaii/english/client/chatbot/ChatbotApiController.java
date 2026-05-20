package com.todaii.english.client.chatbot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.entity.ChatMemory;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.response.PagedResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/chatbot")
public class ChatbotApiController {
  private final ChatbotService chatbotService;

  @GetMapping
  public ResponseEntity<PagedResponse<ChatMemory>> getPagedHistory(
      Authentication authentication,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1")
          int size) {
    Page<ChatMemory> histories = Page.empty();
    if (authentication != null) {
      Long currentUserId = UserUtils.getCurrentUserId(authentication);

      histories = chatbotService.getPagedHistory(currentUserId, page, size);
    }

    PagedResponse<ChatMemory> response =
        new PagedResponse<>(
            histories.getContent(),
            page,
            size,
            histories.getTotalElements(),
            histories.getTotalPages(),
            histories.isFirst(),
            histories.isLast(),
            "timestamp",
            "DESC");

    return ResponseEntity.ok(response);
  }

  // test stream bằng trình duyệt, postman ko stream đc
  @PostMapping
  public Flux<String> sendMessage(
      Authentication authentication,
      @RequestParam @NotBlank(message = "Message must not be blank") String message,
      @RequestParam(defaultValue = "OPENAI") @NotNull(message = "AI Provider is required")
          AiProvider aiProvider) {
    Long currentUserId = null;
    if (authentication != null) {
      currentUserId = UserUtils.getCurrentUserId(authentication);
    }

    return ResponseEntity.ok()
        .body(chatbotService.sendMessage(currentUserId, message, aiProvider))
        .getBody();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteHistory(Authentication authentication) {
    if (authentication != null) {
      Long currentUserId = UserUtils.getCurrentUserId(authentication);
      chatbotService.deleteHistory(currentUserId);
    }

    return ResponseEntity.noContent().build();
  }
}
