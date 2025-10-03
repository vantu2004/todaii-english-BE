package com.todaii.english.server.topic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.Topic;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback
public class TopicRepositoryTests {

	@Autowired
	private TopicRepository topicRepository;

	private Topic topic;

	@BeforeEach
	void setup() {
		// Tạo 1 topic trước khi test
		topic = Topic.builder().name("Travel").alias("travel").enabled(true).isDeleted(false).build();
		topicRepository.save(topic);
	}

	@Test
	@DisplayName("Tạo mới topic")
	void testCreateTopic() {
		Topic saved = topicRepository
				.save(Topic.builder().name("Business English").alias("business-english").enabled(true).build());

		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isGreaterThan(0);
		assertThat(saved.getName()).isEqualTo("Business English");
	}

	@Test
	@DisplayName("Tìm topic theo id khi chưa bị xóa")
	void testFindByIdNotDeleted() {
		Optional<Topic> found = topicRepository.findById(topic.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Travel");
	}

	@Test
	@DisplayName("Tìm topic theo id khi đã bị soft delete")
	void testFindByIdDeleted() {
		// soft delete
		topic.setIsDeleted(true);
		topicRepository.save(topic);

		Optional<Topic> found = topicRepository.findById(topic.getId());
		assertThat(found).isNotPresent();
	}

	@Test
	@DisplayName("Check tồn tại alias")
	void testExistsByAlias() {
		boolean exists = topicRepository.existsByAlias("travel");
		assertThat(exists).isTrue();

		boolean notExists = topicRepository.existsByAlias("non-existing-alias");
		assertThat(notExists).isFalse();
	}

	@Test
	@DisplayName("Update topic")
	void testUpdateTopic() {
		Topic saved = topicRepository.save(Topic.builder().name("Movies").alias("movies").enabled(true).build());

		saved.setName("Films");
		saved.setAlias("films");

		Topic updated = topicRepository.save(saved);

		assertThat(updated.getName()).isEqualTo("Films");
		assertThat(updated.getAlias()).isEqualTo("films");
	}

	@Test
	@DisplayName("Lấy tất cả topic")
	void testFindAll() {
		List<Topic> all = topicRepository.findAll();
		assertThat(all).isNotEmpty();
		assertThat(all.size()).isGreaterThanOrEqualTo(1);
	}
}
