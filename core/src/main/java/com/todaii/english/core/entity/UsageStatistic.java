package com.todaii.english.core.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.todaii.english.shared.enums.ActorType;
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

  @Column(name = "actor_id", nullable = false)
  private Long actorId;

  @Enumerated(EnumType.STRING)
  @Column(name = "actor_type", nullable = false)
  private ActorType actorType;

  @Enumerated(EnumType.STRING)
  @Column(name = "usage_type", length = 32, nullable = false)
  private UsageType usageType;

  @Builder.Default
  @Column(nullable = false)
  private Long quantity = 0L;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String metadata;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Builder.Default
  private LocalDate createdAt = LocalDate.now();
}
