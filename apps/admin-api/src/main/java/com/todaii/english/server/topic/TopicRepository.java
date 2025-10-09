package com.todaii.english.server.topic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
	@Query("SELECT t FROM Topic t WHERE t.id = ?1 AND t.isDeleted = false")
	public Optional<Topic> findById(Long id);

	@Query("SELECT t FROM Topic t WHERE t.isDeleted = false")
	public List<Topic> findAll();
	
	public boolean existsByAlias(String alias);
}
