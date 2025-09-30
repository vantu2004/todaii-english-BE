package com.todaii.english.core.user.user;

import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.core.smtp.SmtpService;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.user.RegisterRequest;
import com.todaii.english.shared.utils.OtpUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private final PasswordHasher passwordHasher;
	private final UserRepository userRepository;
	private final SmtpService smtpService;

	public List<User> getAllUsers() {
		return this.userRepository.findAll();
	}

	public User createUser(RegisterRequest request) {
		if (this.userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		String passwordHash = this.passwordHasher.hash(request.getPassword());

		String otp = OtpUtils.generateOtp();
		this.smtpService.sendVerifyEmail(request.getEmail(), otp);

		User user = User.builder().email(request.getEmail()).passwordHash(passwordHash)
				.displayName(request.getDisplayName()).otp(otp).otpExpiredAt(LocalDateTime.now().plusMinutes(15))
				.status(UserStatus.PENDING).build();

		return this.userRepository.save(user);
	}

}
