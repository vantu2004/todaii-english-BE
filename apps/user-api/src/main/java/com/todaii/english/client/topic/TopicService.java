package com.todaii.english.client.topic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Topic;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {
	private final TopicRepository topicRepository;

	public List<Topic> findAll() {
		return topicRepository.findAll();
	}
}
