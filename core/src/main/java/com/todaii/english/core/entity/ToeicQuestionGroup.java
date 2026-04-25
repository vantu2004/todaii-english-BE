package com.todaii.english.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "toeic_question_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    @JsonIgnore
    private ToeicTest test;

    @Column(nullable = false)
    private Integer partNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String passageText;

    @Column(columnDefinition = "LONGTEXT")
    private String passageTrans;

    @Column(length = 1024)
    private String imageUrl;

    @Column(length = 1024)
    private String audioUrl;
}
