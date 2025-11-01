package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_paragraphs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "article")
public class ArticleParagraph {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "para_order", nullable = false)
	private Integer paraOrder;

	@Lob
	@Column(name = "text_en", columnDefinition = "MEDIUMTEXT", nullable = false)
	private String textEn;

	@Lob
	@Column(name = "text_vi_system", columnDefinition = "MEDIUMTEXT")
	private String textViSystem;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "article_id")
	@JsonIgnore
	private Article article;
}
