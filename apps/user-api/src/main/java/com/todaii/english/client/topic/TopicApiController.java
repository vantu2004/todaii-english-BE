package com.todaii.english.client.topic;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Topic;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topic")
public class TopicApiController {
	private final TopicService topicService;

	@GetMapping
	public ResponseEntity<List<Topic>> getAllTopics() {
		return ResponseEntity.ok(topicService.findAll());
	}
}
