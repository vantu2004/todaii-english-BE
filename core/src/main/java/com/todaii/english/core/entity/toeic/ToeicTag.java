package com.todaii.english.core.entity.toeic;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "toeic_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "part_numbers", nullable = false, length = 64)
  private String partNumbers;

  @Column(nullable = false, length = 191)
  private String name;

  @Column(nullable = false, length = 191)
  private String alias;
}
