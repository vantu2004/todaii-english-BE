package com.todaii.english.server.topic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.TopicType;
import com.todaii.english.shared.enums.error_code.CommonErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.CreateTopicRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {
	private final TopicRepository topicRepository;

	public List<Topic> findAll() {
		return topicRepository.findAll();
	}

	public Topic findById(Long id) {
		return topicRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Topic not found"));
	}

	public Topic create(CreateTopicRequest request) {
		String alias = toAlias(request.getName(), request.getTopicType());
		if (topicRepository.existsByAlias(alias)) {
			throw new BusinessException(409, "Alias already exists: " + alias);
		}

		Topic topic = Topic.builder().name(request.getName()).alias(alias).topicType(request.getTopicType()).build();

		return topicRepository.save(topic);
	}

	public Topic update(Long id, String name) {
		Topic topic = findById(id);

		// Nếu name không thay đổi thì bỏ qua alias check
		if (topic.getName().equalsIgnoreCase(name.trim())) {
			return topic; // không cần save vì không thay đổi gì
		}

		// Sinh alias mới từ name
		String alias = toAlias(name, topic.getTopicType());

		// Check trùng alias với topic khác
		boolean aliasExists = topicRepository.existsByAlias(alias);
		if (aliasExists && !alias.equals(topic.getAlias())) {
			throw new BusinessException(409, "Alias already exists: " + alias);
		}

		// Update dữ liệu
		topic.setName(name.trim());
		topic.setAlias(alias);

		return topicRepository.save(topic);
	}

	private String toAlias(String name, TopicType topicType) {
		return topicType.toString().toLowerCase() + "-" + name.trim().toLowerCase().replaceAll("\\s+", "-");
	}

	public void softDelete(Long id) {
		Topic topic = findById(id);
		topic.setIsDeleted(true);

		topicRepository.save(topic);
	}

	public void toggleEnabled(Long id) {
		Topic topic = findById(id);
		topic.setEnabled(!topic.getEnabled());

		topicRepository.save(topic);
	}
}
