package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


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

    @Column(name = "tag_type", nullable = false, length = 512)
    private String tagType;

//    @ManyToMany(mappedBy = "tags")
//    private Set<ToeicQuestion> questions = new HashSet<>();
}
