package com.todaii.english.client.topic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

}
