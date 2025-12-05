package com.todaii.english.client.event;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.UserEvent;
import com.todaii.english.shared.dto.EventDTO;
import com.todaii.english.shared.enums.EventType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final ObjectMapper objectMapper;

	public void logUser(Long userId, EventType type, Integer quantity, Map<String, Object> metadata) {
		UserEvent ev = UserEvent.builder().userId(userId).eventType(type).quantity(quantity)
				.metadata(serializeMetadata(metadata)).build();

		eventRepository.save(ev);
	}

	private String serializeMetadata(Map<String, Object> metadata) {
		if (metadata == null)
			return null;
		try {
			return objectMapper.writeValueAsString(metadata);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<EventDTO> findByEventType(EventType eventType) {
		List<UserEvent> userEvent = eventRepository.findByEventType(eventType);

		return userEvent.stream().map(event -> {
			Object metaObj = null;

			try {
				metaObj = objectMapper.readValue(event.getMetadata(), Object.class);
			} catch (Exception e) {
				metaObj = null; // hoặc "{}"
			}

			return EventDTO.builder().id(event.getId()).userId(event.getUserId()).eventType(event.getEventType())
					.quantity(event.getQuantity()).metadata(metaObj).createdAt(event.getCreatedAt()).build();

		}).toList();
	}
}
