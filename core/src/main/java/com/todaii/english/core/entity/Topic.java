package com.todaii.english.core.entity;

import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.shared.enums.TopicType;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 191, unique = true, nullable = false)
	private String name;

	@Column(length = 191, unique = true, nullable = false)
	private String alias;

	@Enumerated(EnumType.STRING)
	@Column(name = "topic_type", length = 32)
	private TopicType topicType;

	@Builder.Default
	private Boolean enabled = false;

	@Builder.Default
	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

}
