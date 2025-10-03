package com.todaii.english.server.topic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.error_code.CommonErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {
	private final TopicRepository topicRepository;

	public List<Topic> findAll() {
		return topicRepository.findAll();
	}

	public Topic findById(Long id) {
		return topicRepository.findById(id).orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
	}

	public Topic create(String name) {
		String alias = toAlias(name);
		if (topicRepository.existsByAlias(alias)) {
			throw new BusinessException(409, "Alias already exists: " + alias);
		}

		Topic topic = Topic.builder().name(name).alias(alias).build();

		return topicRepository.save(topic);
	}

	public Topic update(Long id, String name) {
		Topic topic = findById(id);

		// Nếu name không thay đổi thì bỏ qua alias check
		if (topic.getName().equalsIgnoreCase(name.trim())) {
			return topic; // không cần save vì không thay đổi gì
		}

		// Sinh alias mới từ name
		String alias = toAlias(name);

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

	private String toAlias(String name) {
		return name.trim().toLowerCase().replaceAll("\\s+", "-"); // thay nhiều khoảng trắng bằng 1 dấu -
	}

	public void softDelete(Long id) {
		Topic topic = findById(id);
		topic.setIsDeleted(true);

		topicRepository.save(topic);
	}

	public Topic toggleEnabled(Long id) {
		Topic topic = findById(id);
		topic.setEnabled(!topic.getEnabled());

		return topicRepository.save(topic);
	}
}
