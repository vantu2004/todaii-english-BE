package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "article_paragraphs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

	@ManyToOne
	@JoinColumn(name = "article_id")
	@JsonIgnore
	private Article article;
}
