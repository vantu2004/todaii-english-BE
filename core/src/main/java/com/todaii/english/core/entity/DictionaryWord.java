package com.todaii.english.core.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

  // Hibernate 6 sẽ tự động xử lý kiểu dữ liệu JSON với MySQL
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "json_data", columnDefinition = "json")
  private String jsonData;
}
