package com.todaii.english.core.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
    name = "dictionary_words",
    indexes = @Index(name = "idx_word", columnList = "word", unique = true))
@Getter
@Setter
public class DictionaryWord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 64, nullable = false)
  private String word;
}
