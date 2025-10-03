package com.todaii.english.client.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.UserEvent;

@Repository
public interface EventRepository extends JpaRepository<UserEvent, Long> {

}
