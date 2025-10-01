package com.todaii.english.core.user.user;

import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.core.smtp.SmtpService;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.VerifyOtpRequest;
import com.todaii.english.shared.request.user.RegisterRequest;
import com.todaii.english.shared.request.user.ResetPasswordRequest;
import com.todaii.english.shared.utils.OtpUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
	private final PasswordHasher passwordHasher;
	private final UserRepository userRepository;
	private final SmtpService smtpService;

	private String CLIENT_URL = "http://localhost:5173";

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

	public void verifyOtp(VerifyOtpRequest verifyOtpRequest) {
		User user = this.userRepository.findActiveByEmail(verifyOtpRequest.getEmail())
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		if (user.getOtp() == null || user.getOtpExpiredAt() == null) {
			throw new BusinessException(AuthErrorCode.OTP_NOT_FOUND);
		}
		if (user.getOtpExpiredAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException(AuthErrorCode.OTP_EXPIRED);
		}
		if (!verifyOtpRequest.getOtp().equals(user.getOtp())) {
			throw new BusinessException(AuthErrorCode.OTP_INVALID);
		}

		// Nếu hợp lệ thì clear OTP và active account
		user.setOtp(null);
		user.setOtpExpiredAt(null);
		user.setEnabled(true);
		user.setStatus(UserStatus.ACTIVE);
		user.setEmailVerifiedAt(LocalDateTime.now());

		this.userRepository.save(user);
	}

	public void resendOtp(String email) {
		User user = this.userRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		if (user.isEnabled() || !user.getStatus().equals(UserStatus.PENDING)) {
			throw new BusinessException(AuthErrorCode.ALREADY_VERIFIED);
		}

		String otp = OtpUtils.generateOtp();

		user.setOtp(otp);
		user.setOtpExpiredAt(LocalDateTime.now().plusMinutes(15));
		this.userRepository.save(user);

		this.smtpService.sendVerifyEmail(email, otp);
	}

	public void updateLastLogin(String email) {
		User user = this.userRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		user.setLastLoginAt(LocalDateTime.now());

		this.userRepository.save(user);
	}

	public void forgotPassword(String email) {
		User user = this.userRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		if (!user.isEnabled()) {
			throw new BusinessException(AuthErrorCode.USER_NOT_ENABLED);
		}

		String token = UUID.randomUUID().toString();
		user.setResetPasswordToken(token);
		user.setResetPasswordExpiredAt(LocalDateTime.now().plusMinutes(15));
		this.userRepository.save(user);

		String resetURL = CLIENT_URL + "/reset-pasword/" + token;

		this.smtpService.sendForgotPasswordEmail(email, resetURL);
	}

	public void resetPassword(ResetPasswordRequest request) {
		// Tìm user có resetPasswordToken khớp
		User user = this.userRepository.findByResetPasswordToken(request.getResetPasswordToken())
				.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND));

		// Check expire
		if (user.getResetPasswordExpiredAt() == null
				|| user.getResetPasswordExpiredAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
		}

		user.setPasswordHash(this.passwordHasher.hash(request.getPassword()));
		user.setResetPasswordToken(null);
		user.setResetPasswordExpiredAt(null);

		this.userRepository.save(user);
	}

}
