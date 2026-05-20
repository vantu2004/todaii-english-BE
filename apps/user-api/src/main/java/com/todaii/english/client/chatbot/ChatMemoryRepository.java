package com.todaii.english.client.chatbot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.todaii.english.core.entity.ChatMemory;

public interface ChatMemoryRepository extends JpaRepository<ChatMemory, Long> {
  Page<ChatMemory> findByConversationIdOrderByTimestampDesc(
      String conversationId, Pageable pageable);

  void deleteAllByConversationId(String conversationId);
}
