package com.todaii.english.core.entity;

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

    @Column(nullable = false, length = 191)
    private String name;

    @Column(nullable = false, length = 512)
    private String tagType;

}
