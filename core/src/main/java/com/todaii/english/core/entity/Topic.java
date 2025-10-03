package com.todaii.english.core.entity;

import java.util.Set;
import java.util.HashSet;

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

	@Column(length = 191, unique = true)
	private String alias;

	@Builder.Default
	private Boolean enabled = true;

	@Builder.Default
	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	// Quan hệ N-N với DictionarySense
	@ManyToMany(mappedBy = "topics")
	@Builder.Default
	private Set<DictionarySense> senses = new HashSet<>();
}
