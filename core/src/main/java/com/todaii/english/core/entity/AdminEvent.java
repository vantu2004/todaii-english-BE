package com.todaii.english.core.entity;

import com.todaii.english.shared.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private AdminEventModule module;

	@Enumerated(EnumType.STRING)
	private AdminEventAction action;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private EventOutcome outcome = EventOutcome.SUCCESS;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;
}
