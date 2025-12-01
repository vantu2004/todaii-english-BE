package com.todaii.english.server.event;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.AdminEvent;
import com.todaii.english.shared.dto.AdminEventDTO;
import com.todaii.english.shared.enums.EventType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final ObjectMapper objectMapper;

	public void logAdmin(Long adminId, EventType type, Integer quantity, Map<String, Object> metadata) {
		// Các event khác (DICTIONARY_API, AI_REQUEST)
		AdminEvent ev = AdminEvent.builder().adminId(adminId).eventType(type).quantity(quantity)
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

	public List<AdminEventDTO> findByEventType(EventType eventType) {
		List<AdminEvent> adminEvents = eventRepository.findByEventType(eventType);

		return adminEvents.stream().map(event -> {
			Object metaObj = null;

			try {
				metaObj = objectMapper.readValue(event.getMetadata(), Object.class);
			} catch (Exception e) {
				metaObj = null; // hoặc "{}"
			}

			return AdminEventDTO.builder().id(event.getId()).adminId(event.getAdminId()).eventType(event.getEventType())
					.quantity(event.getQuantity()).metadata(metaObj).createdAt(event.getCreatedAt()).build();

		}).toList();
	}
}
