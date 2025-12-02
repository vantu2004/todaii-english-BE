package com.todaii.english.shared.dto;

import java.time.LocalDateTime;

import com.todaii.english.shared.enums.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
	private Long id;
	private Long userId;
	private EventType eventType;
	private Integer quantity;
	private Object metadata;
	private LocalDateTime createdAt;
}
