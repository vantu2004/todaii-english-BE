package com.todaii.english.core.user.user;

import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private final PasswordHasher passwordHasher;
	private final UserRepository userRepository;

	public List<User> getAllUsers() {
		return this.userRepository.findAll();
	}

	public User createUser(String email, String rawPassword, String displayName) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		User user = User.builder().email(email).passwordHash(this.passwordHasher.hash(rawPassword))
				.displayName(displayName).status(UserStatus.PENDING).build();

		return this.userRepository.save(user);
	}

}
