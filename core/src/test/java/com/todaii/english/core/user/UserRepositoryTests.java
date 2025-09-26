package com.todaii.english.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.user.user.User;
import com.todaii.english.core.user.user.UserRepository;
import com.todaii.english.shared.enums.UserStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testCreateUser() {
		User user = User.builder().email("test@example.com").passwordHash("hashedPassword123").displayName("Test User")
				.status(UserStatus.PENDING).build();

		User savedUser = userRepository.save(user);

		// Kiểm tra ID được tạo
		assertThat(savedUser.getId()).isNotNull();

		// Kiểm tra default value
		assertThat(savedUser.getIsDeleted()).isFalse();

		// Kiểm tra timestamp tự động set
		assertThat(savedUser.getCreatedAt()).isNotNull();
		assertThat(savedUser.getUpdatedAt()).isNotNull();
	}

	@Test
	public void testUpdateUser() throws InterruptedException {
		User user = this.userRepository.findByEmail("test@example.com").get();
		assertThat(user).isNotNull();

		LocalDateTime oldUpdated = user.getUpdatedAt();

		user.setStatus(UserStatus.LOCKED);
		User savedUser = this.userRepository.save(user);

		LocalDateTime newUpdated = savedUser.getUpdatedAt();

		assertThat(newUpdated).isAfterOrEqualTo(oldUpdated);

		System.out.println(oldUpdated);
		System.out.println(newUpdated);
	}

	@Test
	public void testSoftDeleteFlag() {
		User user = this.userRepository.findByEmail("test@example.com").get();
		assertThat(user).isNotNull();

		user.setIsDeleted(true);
		user.setDeletedAt(LocalDateTime.now());
		userRepository.save(user);

		User deleted = userRepository.findById(user.getId()).orElseThrow();
		assertThat(deleted.getIsDeleted()).isTrue();
		assertThat(deleted.getDeletedAt()).isNotNull();
	}
}
