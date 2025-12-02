package com.todaii.english.client.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.UserEvent;
import com.todaii.english.shared.enums.EventType;

@Repository
public interface EventRepository extends JpaRepository<UserEvent, Long> {
	public List<UserEvent> findByEventType(EventType eventType);

}
