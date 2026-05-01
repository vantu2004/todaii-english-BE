package com.todaii.english.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name = "toeic_collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicCollection {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 191)
  private String name;

  @Column(length = 191, nullable = false)
  private String alias;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String description;

  @Builder.Default private Boolean enabled = false;

  @Builder.Default
  @Column(name = "is_deleted")
  private Boolean isDeleted = false;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
