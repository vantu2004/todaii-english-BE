package com.todaii.english.client.event;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.UserEvent;
import com.todaii.english.shared.enums.EventType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final ObjectMapper objectMapper;

	public void logUser(Long userId, EventType type, Integer quantity, Map<String, Object> metadata)
			throws JsonProcessingException {
		UserEvent ev = new UserEvent();
		ev.setUserId(userId);
		ev.setEventType(type);
		ev.setQuantity(quantity != null ? quantity : 0);
		ev.setMetadata(metadata != null ? objectMapper.writeValueAsString(metadata) : null);

		eventRepository.save(ev);
	}
}
