package com.todaii.english.server.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.UserEvent;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
	public List<UserEvent> findByUserId(Long id);
}
