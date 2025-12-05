package com.todaii.english.core.entity;

import java.time.LocalDateTime;

import com.todaii.english.shared.enums.EventType;

public interface BaseEvent {
	EventType getEventType();

	Integer getQuantity();

	LocalDateTime getCreatedAt();

	String getMetadata();
}
