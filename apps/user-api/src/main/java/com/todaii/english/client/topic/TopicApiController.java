package com.todaii.english.client.topic;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.TopicType;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topic")
@Tag(name = "Topic", description = "APIs for managing topics")
public class TopicApiController {
	private final TopicService topicService;

	@GetMapping
	@Operation(summary = "Get all topics", description = "Retrieve all topics of a specific type")
	public ResponseEntity<List<Topic>> getAllTopics(@RequestParam(required = true) TopicType topicType) {
		return ResponseEntity.ok(topicService.findAll(topicType));
	}
}
