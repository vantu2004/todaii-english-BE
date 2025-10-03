package com.todaii.english.server.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.AdminEvent;

@Repository
public interface AdminEventRepository extends JpaRepository<AdminEvent, Long> {
	public List<AdminEvent> findByAdminId(Long id);
}
