package com.todaii.english.server.event;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminEvent;
import com.todaii.english.core.entity.UserEvent;
import com.todaii.english.shared.enums.AdminEventAction;
import com.todaii.english.shared.enums.AdminEventModule;
import com.todaii.english.shared.enums.EventOutcome;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final AdminEventRepository adminEventRepository;
	private final UserEventRepository userEventRepository;

	public List<AdminEvent> getAllAdminEvents() {
		return adminEventRepository.findAll();
	}

	public List<AdminEvent> getEventsByAdminId(Long id) {
		return adminEventRepository.findByAdminId(id);
	}

	public void log(Admin admin, AdminEventModule module, AdminEventAction action, EventOutcome outcome) {
		AdminEvent event = AdminEvent.builder().admin(admin).module(module).action(action).outcome(outcome).build();
		adminEventRepository.save(event);
	}

	public List<UserEvent> getAllUserEvents() {
		return userEventRepository.findAll();
	}

	public List<UserEvent> getEventsByUserId(Long id) {
		return userEventRepository.findByUserId(id);
	}
}
