package com.todaii.english.client.user;

import com.todaii.english.client.article.ArticleRepository;
import com.todaii.english.client.video.VideoRepository;
import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.User;
import com.todaii.english.core.entity.Video;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.core.smtp.SmtpService;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.VerifyOtpRequest;
import com.todaii.english.shared.request.client.RegisterRequest;
import com.todaii.english.shared.request.client.ResetPasswordRequest;
import com.todaii.english.shared.utils.OtpUtils;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
	private final PasswordHasher passwordHasher;
	private final UserRepository userRepository;
	private final SmtpService smtpService;
	private final ModelMapper modelMapper;
	private final ArticleRepository articleRepository;
	private final VideoRepository videoRepository;
	private final CloudinaryPort cloudinaryPort;

	private String CLIENT_URL = "http://localhost:5173";

	public UserDTO createUser(RegisterRequest request) {
		if (this.userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		String passwordHash = this.passwordHasher.hash(request.getPassword());

		String otp = OtpUtils.generateOtp();
		this.smtpService.sendVerifyEmail(request.getEmail(), otp);

		User user = User.builder().email(request.getEmail()).passwordHash(passwordHash)
				.displayName(request.getDisplayName()).otp(otp).otpExpiredAt(LocalDateTime.now().plusMinutes(15))
				.status(UserStatus.PENDING).build();

		User savedUser = this.userRepository.save(user);
		UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

		return userDTO;
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

		if (user.getEnabled() || !user.getStatus().equals(UserStatus.PENDING)) {
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

		if (!user.getEnabled()) {
			throw new BusinessException(AuthErrorCode.USER_NOT_ENABLED);
		}

		String token = UUID.randomUUID().toString();
		user.setResetPasswordToken(token);
		user.setResetPasswordExpiredAt(LocalDateTime.now().plusMinutes(15));
		this.userRepository.save(user);

		String resetURL = CLIENT_URL + "/client/reset-password/" + token;

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

	public UserDTO getUserById(Long id) {
		User user = this.userRepository.findById(id)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		return modelMapper.map(user, UserDTO.class);
	}

	public UserDTO updateProfile(Long id, UpdateProfileRequest request) {
		User user = this.userRepository.findById(id)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		// Xử lý đổi mật khẩu
		if (StringUtils.hasText(request.getNewPassword())) {
			if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}

			if (!StringUtils.hasText(request.getOldPassword())
					|| !passwordHasher.matches(request.getOldPassword(), user.getPasswordHash())) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INCORRECT);
			}

			user.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
		}

		String avatar = request.getAvatarUrl();
		if (StringUtils.hasText(avatar) && request.getAvatarUrl().startsWith("data:image")) {
			String uploadedUrl = cloudinaryPort.uploadImage(avatar, "user_avatars");
			user.setAvatarUrl(uploadedUrl);

		} else {
			user.setAvatarUrl(user.getAvatarUrl());
		}

		user.setDisplayName(request.getDisplayName());

		User savedUser = this.userRepository.save(user);
		UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

		return userDTO;
	}

	public void toggleSavedArticle(Long currentUserId, Long articleId) {
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessException(404, "Article not found"));

		Set<Article> savedArticles = user.getSavedArticles();

		if (savedArticles.contains(article)) {
			savedArticles.remove(article);
		} else {
			savedArticles.add(article);
		}

		userRepository.save(user);
	}

	public void toggleSavedVideo(Long currentUserId, Long videoId) {
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		Video video = videoRepository.findById(videoId)
				.orElseThrow(() -> new BusinessException(404, "Video not found"));

		Set<Video> savedVideos = user.getSavedVideos();

		if (savedVideos.contains(video)) {
			savedVideos.remove(video);
		} else {
			savedVideos.add(video);
		}

		userRepository.save(user);
	}

}
