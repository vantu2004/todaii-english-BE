package com.todaii.english.core.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.AiProvider;
import com.todaii.english.shared.enums.UsageType;

import lombok.*;

@Entity
@Table(name = "usage_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageStatistic {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ----- COMMON -----

  // mặc định = 0 thay vì null
  @Column(name = "actor_id")
  @Builder.Default
  private Long actorId = 0L;

  @Enumerated(EnumType.STRING)
  @Column(name = "actor_type", length = 32, nullable = false)
  private ActorType actorType;

  @Enumerated(EnumType.STRING)
  @Column(name = "usage_type", length = 32, nullable = false)
  private UsageType usageType;

  @Builder.Default
  @Column(nullable = false)
  private Long quantity = 0L;

  // ----- AI STAT -----

  @Column(name = "input_token")
  @Builder.Default
  private Long inputToken = 0L;

  @Column(name = "output_token")
  @Builder.Default
  private Long outputToken = 0L;

  @Column(name = "total_token")
  @Builder.Default
  private Long totalToken = 0L;

  @Enumerated(EnumType.STRING)
  @Column(name = "ai_provider", length = 32)
  private AiProvider aiProvider;

  private String model;

  // ----- YOUTUBE DATA API V3 STAT -----
  @Builder.Default private Long quota = 0L;

  // ----- GOOGLE TRANSLATE STAT -----
  @Column(name = "char_quantity")
  @Builder.Default
  private Long charQuantity = 0L;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Builder.Default
  private LocalDate createdAt = LocalDate.now();
}
