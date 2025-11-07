package com.todaii.english.server.topic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
	@Query("SELECT t FROM Topic t WHERE t.id = ?1 AND t.isDeleted = false")
	public Optional<Topic> findById(Long id);

	@Deprecated
	@Query("SELECT t FROM Topic t WHERE t.isDeleted = false")
	public List<Topic> findAll();

	@Query("""
			SELECT t FROM Topic t
			WHERE t.isDeleted = false
			AND (
			    ?1 IS NULL
			    OR STR(t.id) LIKE CONCAT('%', ?1, '%')
			    OR LOWER(t.name) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(t.alias) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(t.topicType) LIKE LOWER(CONCAT('%', ?1, '%'))
			)
			""")
	public Page<Topic> findAllActive(String keyword, Pageable pageable);

	public boolean existsByAlias(String alias);
}
