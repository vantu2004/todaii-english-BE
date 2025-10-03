package com.todaii.english.core.entity;

import com.todaii.english.shared.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private UserEventModule module;

	@Enumerated(EnumType.STRING)
	private UserEventAction action;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private EventOutcome outcome = EventOutcome.SUCCESS;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
