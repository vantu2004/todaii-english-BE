package com.todaii.english.core.entity.vocabulary;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.shared.enums.CefrLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocab_decks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabDeck {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 191, nullable = false)
  private String name;

  @Lob
  @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(length = 32)
  private CefrLevel cefrLevel;

  @Builder.Default private Boolean enabled = false;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // quan hệ 1 chiều
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "deck_groups",
      joinColumns = @JoinColumn(name = "deck_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id"))
  @Builder.Default
  private Set<VocabGroup> groups = new HashSet<>();

  // quan hệ 1 chiều
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "deck_words",
      joinColumns = @JoinColumn(name = "deck_id"),
      inverseJoinColumns = @JoinColumn(name = "dict_word_id"))
  @Builder.Default
  private Set<DictionaryWord> words = new HashSet<>();
}
