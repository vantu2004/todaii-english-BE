package com.todaii.english.server.topic;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.request.server.CreateTopicRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/topic")
public class TopicApiController {
	private final TopicService topicService;

	@GetMapping
	public ResponseEntity<?> getAll() {
		List<Topic> topics = topicService.findAll();
		if (topics.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(topics);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return ResponseEntity.ok(topicService.findById(id));
	}

	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody CreateTopicRequest createTopicRequest) {
		return ResponseEntity.status(201).body(topicService.create(createTopicRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestParam @NotBlank @Length(max = 191) String name) {
		return ResponseEntity.ok(topicService.update(id, name));
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		topicService.softDelete(id);
	}

	@PatchMapping("/{id}/enabled")
	public void toggleEnabled(@PathVariable Long id) {
		topicService.toggleEnabled(id);
	}
}
