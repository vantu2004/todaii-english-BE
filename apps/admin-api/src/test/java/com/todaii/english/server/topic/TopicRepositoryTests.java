package com.todaii.english.server.topic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.TopicType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
class TopicRepositoryTests {

	@Autowired
	private TopicRepository topicRepository;

	private Topic topic;

	@BeforeEach
	void setup() {
		topic = Topic.builder().name("Travel").alias("article-travel").topicType(TopicType.ARTICLE).enabled(true)
				.isDeleted(false).build();
		topicRepository.save(topic);
	}

	@Test
	@DisplayName("Tạo mới topic")
	void testCreateTopic() {
		Topic saved = topicRepository.save(Topic.builder().name("Business English").alias("article-business-english")
				.topicType(TopicType.ARTICLE).enabled(true).isDeleted(false).build());

		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isGreaterThan(0);
		assertThat(saved.getName()).isEqualTo("Business English");
	}

	@Test
	@DisplayName("Tìm topic theo id chưa bị xóa")
	void testFindByIdNotDeleted() {
		Optional<Topic> found = topicRepository.findById(topic.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Travel");
	}

	@Test
	@DisplayName("Tìm topic theo id đã bị soft delete")
	void testFindByIdDeleted() {
		topic.setIsDeleted(true);
		topicRepository.save(topic);

		Optional<Topic> found = topicRepository.findById(topic.getId());
		assertThat(found).isNotPresent();
	}

	@Test
	@DisplayName("Kiểm tra tồn tại alias")
	void testExistsByAlias() {
		boolean exists = topicRepository.existsByAlias("article-travel");
		assertThat(exists).isTrue();

		boolean notExists = topicRepository.existsByAlias("article-non-existing");
		assertThat(notExists).isFalse();
	}

	@Test
	@DisplayName("Tìm kiếm topic có keyword")
	void testFindAllActive_withKeyword() {
		Page<Topic> result = topicRepository.findAllActive("travel", PageRequest.of(0, 5));
		assertThat(result.getContent()).isNotEmpty();
		assertThat(result.getContent().get(0).getAlias()).contains("travel");
	}

	@Test
	@DisplayName("Tìm kiếm topic khi keyword null")
	void testFindAllActive_withoutKeyword() {
		Page<Topic> result = topicRepository.findAllActive(null, PageRequest.of(0, 5));
		assertThat(result.getTotalElements()).isGreaterThan(0);
	}
}
