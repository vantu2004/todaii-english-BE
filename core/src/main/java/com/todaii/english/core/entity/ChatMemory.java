package com.todaii.english.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "spring_ai_chat_memory")
public class ChatMemory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "conversation_id", nullable = false)
  private String conversationId;

  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String content;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private LocalDateTime timestamp;
}
