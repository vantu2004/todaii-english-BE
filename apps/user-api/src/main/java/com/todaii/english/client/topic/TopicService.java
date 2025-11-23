package com.todaii.english.client.topic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.TopicType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {
	private final TopicRepository topicRepository;

	public List<Topic> findAll(TopicType topicType) {
		return topicRepository.findAllByTopicType(topicType);
	}
}
