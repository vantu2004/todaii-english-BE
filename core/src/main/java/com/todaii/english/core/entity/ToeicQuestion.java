package com.todaii.english.core.entity;

import com.todaii.english.shared.enums.Answer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "toeic_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> Test
    @ManyToOne
    @JoinColumn(name = "test_id")
    private ToeicTest test;

    // FK -> Group (nullable)
    @ManyToOne
    @JoinColumn(name = "group_id")
    private ToeicQuestionGroup group;

    @Column(nullable = false)
    private Integer partNumber;

    @Column(nullable = false)
    private Integer questionNo;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String question;

    @Column(length = 1024)
    private String imageUrl;

    @Column(length = 1024)
    private String audioUrl;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String optionA;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String optionB;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String optionC;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String optionD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Answer correctAns;

    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(columnDefinition = "LONGTEXT")
    private String translation;
}