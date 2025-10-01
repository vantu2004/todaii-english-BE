package com.todaii.english.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

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

	@Test
	public void testFindActiveByEmail() {
		// User test@example.com đã bị xóa trong testSoftDeleteFlag => tạo mới user khác
		User activeUser = User.builder().email("active@example.com").passwordHash("hashedPassword456")
				.displayName("Active User").status(UserStatus.ACTIVE).build();

		activeUser = userRepository.save(activeUser);

		// Tìm user active
		User found = userRepository.findActiveByEmail("active@example.com").orElseThrow();
		assertThat(found.getEmail()).isEqualTo("active@example.com");
		assertThat(found.getIsDeleted()).isFalse();

		// Đánh dấu xóa
		found.setIsDeleted(true);
		userRepository.save(found);

		// Khi gọi lại sẽ không tìm thấy nữa
		Optional<User> deleted = userRepository.findActiveByEmail("active@example.com");
		assertThat(deleted).isEmpty();
	}

	@Test
	public void testFindByResetPasswordToken() {
		User resetUser = User.builder().email("reset@example.com").passwordHash("resetPass").displayName("Reset User")
				.status(UserStatus.ACTIVE).resetPasswordToken("reset-token-123").build();

		resetUser = userRepository.save(resetUser);

		// Tìm bằng token
		User found = userRepository.findByResetPasswordToken("reset-token-123").orElseThrow();
		assertThat(found.getEmail()).isEqualTo("reset@example.com");

		// Đánh dấu xóa
		found.setIsDeleted(true);
		userRepository.save(found);

		// Token không còn hợp lệ
		Optional<User> deleted = userRepository.findByResetPasswordToken("reset-token-123");
		assertThat(deleted).isEmpty();
	}

	@Test
	public void testFindByIdActiveOnly() {
		User user = User.builder().email("findid@example.com").passwordHash("pass123").displayName("Find By Id User")
				.status(UserStatus.ACTIVE).build();

		user = userRepository.save(user);

		// Tìm thấy khi chưa xóa
		User found = userRepository.findById(user.getId()).orElseThrow();
		assertThat(found.getIsDeleted()).isFalse();

		// Xóa mềm
		found.setIsDeleted(true);
		found.setDeletedAt(LocalDateTime.now());
		userRepository.save(found);

		// Không tìm thấy khi isDeleted = true
		Optional<User> deleted = userRepository.findById(user.getId());
		assertThat(deleted).isEmpty();
	}

	@Test
	public void testFindByEmail() {
		User user = User.builder().email("simple@example.com").passwordHash("simplepass").displayName("Simple User")
				.status(UserStatus.PENDING).build();

		userRepository.save(user);

		Optional<User> found = userRepository.findByEmail("simple@example.com");
		assertThat(found).isPresent();
		assertThat(found.get().getDisplayName()).isEqualTo("Simple User");
	}

}
