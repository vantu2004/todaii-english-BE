package com.todaii.english.client.event;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.User;
import com.todaii.english.core.entity.UserEvent;
import com.todaii.english.shared.enums.EventOutcome;
import com.todaii.english.shared.enums.UserEventAction;
import com.todaii.english.shared.enums.UserEventModule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;

	public void log(User user, UserEventModule module, UserEventAction action, EventOutcome outcome) {
		UserEvent event = UserEvent.builder().user(user).module(module).action(action).outcome(outcome).build();
		eventRepository.save(event);
	}
}
