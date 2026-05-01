package com.todaii.english.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(nullable = false, length = 512)
    private String title;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "test_type")
    private TestType testType = TestType.TOEIC_LR;

    // 120 minutes
    @Builder.Default
    @Column(nullable = false)
    private Integer duration = 120;

    @Column(name = "audio_url", length = 1024)
    private String audioUrl;

    @Column(length = 1024)
    private String thumbnail;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TestStatus status = TestStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToeicPassage> passages = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToeicQuestion> questions = new ArrayList<>();

    // quan hệ 1 chiều
    @ManyToOne
    @JoinColumn(name = "collection_id")
    private ToeicCollection collection;

    // ignore luôn để giấu thông tin admin
    // quan hệ 1 chiều, hạn chế việc khi query admin thì nó query quá nhiều thông tin liên quan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private Admin creator;

    // quan hệ 1 chiều
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private Admin updatedBy;

}