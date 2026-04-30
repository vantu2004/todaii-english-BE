package com.todaii.english.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.Answer;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "test_id")
    @JsonIgnore
    private ToeicTest test;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
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

    @Column(name = "option_a", columnDefinition = "MEDIUMTEXT")
    private String optionA;

    @Column(name = "option_b", columnDefinition = "MEDIUMTEXT")
    private String optionB;

    @Column(name = "option_c", columnDefinition = "MEDIUMTEXT")
    private String optionC;

    @Column(name = "option_d", columnDefinition = "MEDIUMTEXT")
    private String optionD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Answer correctAns;

    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(columnDefinition = "LONGTEXT")
    private String translation;

    @ManyToMany
    @JoinTable(name = "toeic_question_tags", joinColumns = @JoinColumn(name = "question_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Builder.Default
    private Set<ToeicTag> tags = new HashSet<>();
}