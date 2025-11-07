package com.todaii.english.server.topic;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
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
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/topic")
public class TopicApiController {
	private final TopicService topicService;

	@Deprecated
	public ResponseEntity<List<Topic>> getAll() {
		List<Topic> topics = topicService.findAll();
		if (topics.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(topics);
	}

	@GetMapping
	public ResponseEntity<PagedResponse<Topic>> getAllPaged(@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction, @RequestParam(required = false) String keyword) {
		Page<Topic> topics = topicService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<Topic> response = new PagedResponse<Topic>(topics.getContent(), page, size,
				topics.getTotalElements(), topics.getTotalPages(), topics.isFirst(), topics.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Topic> getById(@PathVariable Long id) {
		return ResponseEntity.ok(topicService.findById(id));
	}

	@PostMapping
	public ResponseEntity<Topic> create(@Valid @RequestBody CreateTopicRequest createTopicRequest) {
		return ResponseEntity.status(201).body(topicService.create(createTopicRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Topic> update(@PathVariable Long id, @RequestParam @NotBlank @Length(max = 191) String name) {
		return ResponseEntity.ok(topicService.update(id, name));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		topicService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		topicService.softDelete(id);
		return ResponseEntity.ok().build();
	}

}
