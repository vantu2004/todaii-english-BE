package com.todaii.english.core.entity.toeic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.core.entity.admin.Admin;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;

import lombok.*;

@Entity
@Table(name = "toeic_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ToeicTest extends MediaUrl {
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

  @Column(columnDefinition = "MEDIUMTEXT")
  private String description;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private TestStatus status = TestStatus.DRAFT;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", insertable = false)
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
  @CreatedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false, updatable = false)
  @JsonIgnore
  private Admin createdBy;

  // quan hệ 1 chiều
  @LastModifiedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by", insertable = false)
  @JsonIgnore
  private Admin updatedBy;

  @ManyToMany(mappedBy = "savedTests", fetch = FetchType.LAZY)
  @JsonIgnore
  @Builder.Default
  private Set<User> users = new HashSet<>();
}
