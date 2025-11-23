package com.todaii.english.client.topic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.TopicType;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

	@Query("""
				SELECT t FROM Topic t
				WHERE t.enabled = true
				AND t.isDeleted = false
				AND t.topicType = ?1
			""")
	List<Topic> findAllByTopicType(TopicType topicType);
}
