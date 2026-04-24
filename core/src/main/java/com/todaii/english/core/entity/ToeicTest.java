package com.todaii.english.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "toeic_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    @JsonIgnore
    private ToeicCollection collection;

    @Column(nullable = false, length = 512)
    private String title;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType testType = TestType.TOEIC_LR;

    @Builder.Default
    @Column(nullable = false)
    private Integer duration = 120;

    @Column(length = 1024)
    private String audioUrl;

    @Column(length = 1024)
    private String thumbnail;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TestStatus status = TestStatus.DRAFT;

    private Long creatorId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // RELATION
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<ToeicQuestionGroup> groups;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<ToeicQuestion> questions;
}