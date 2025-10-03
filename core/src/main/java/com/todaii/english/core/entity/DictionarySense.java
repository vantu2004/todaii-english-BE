package com.todaii.english.core.entity;

import java.util.List;

import com.todaii.english.shared.enums.PartOfSpeech;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dictionary_senses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DictionarySense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private PartOfSpeech pos;
	
	@Lob
	private String definition; 
	@Lob
	private String example;

	private List<String> synonyms;

	private List<String> collocations;

	@ManyToOne
	@JoinColumn(name = "entry_id", nullable = false)
	private DictionaryEntry entry;

	// Quan hệ N-N với topic
	@ManyToMany
	@JoinTable(name = "sense_topics", joinColumns = @JoinColumn(name = "sense_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
	private List<Topic> topics;
}
