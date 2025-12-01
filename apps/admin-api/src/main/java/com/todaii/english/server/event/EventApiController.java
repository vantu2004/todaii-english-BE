package com.todaii.english.server.event;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.dto.AdminEventDTO;
import com.todaii.english.shared.enums.EventType;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventApiController {
	private final EventService eventService;

	@GetMapping("/type")
	public ResponseEntity<List<AdminEventDTO>> getAdminEventsByType(@RequestParam EventType eventType) {
		return ResponseEntity.ok(eventService.findByEventType(eventType));
	}
}
