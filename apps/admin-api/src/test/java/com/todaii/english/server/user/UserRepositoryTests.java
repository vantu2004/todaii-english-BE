package com.todaii.english.server.user;

import com.todaii.english.core.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("findAllActive - trả về danh sách người dùng hợp lệ (phân trang + sort)")
	void findAllActive_withPagingAndSort() {
		Pageable pageable = PageRequest.of(0, 5, Sort.by("email").ascending());
		Page<User> result = userRepository.findAllActive(null, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent().size()).isLessThanOrEqualTo(5);
	}

	@Test
	@DisplayName("findAllActive - lọc theo keyword (email hoặc displayName)")
	void findAllActive_withKeyword() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<User> result = userRepository.findAllActive("john", pageable);

		assertThat(result).isNotNull();
		result.forEach(u -> assertThat(
				u.getEmail().toLowerCase().contains("john") || u.getDisplayName().toLowerCase().contains("john"))
				.isTrue());
	}

	@Test
	@DisplayName("findById - trả về Optional<User> hợp lệ khi có ID")
	void findById_success() {
		Optional<User> userOpt = userRepository.findById(2L);
		assertThat(userOpt).isPresent();
	}
}
