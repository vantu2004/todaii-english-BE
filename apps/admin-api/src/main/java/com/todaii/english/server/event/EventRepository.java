package com.todaii.english.server.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.AdminEvent;
import com.todaii.english.shared.enums.EventType;

@Repository
public interface EventRepository extends JpaRepository<AdminEvent, Long> {
	public List<AdminEvent> findByEventType(EventType eventType);

}
