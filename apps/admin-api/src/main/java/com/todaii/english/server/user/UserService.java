package com.todaii.english.server.user;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.User;
import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.UpdateUserRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;
	private final ModelMapper modelMapper;

	@Deprecated
	public List<UserDTO> findAll() {
		List<User> users = userRepository.findAll();
		List<UserDTO> userDTOs = users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();

		return userDTOs;
	}

	public Page<UserDTO> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		Page<User> userPage = userRepository.findAllActive(keyword, pageable);

		return userPage.map(user -> modelMapper.map(user, UserDTO.class));
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}

	public UserDTO findUserDTOById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);

		return userDTO;
	}

	public UserDTO update(Long id, @Valid UpdateUserRequest request) {
		User user = findById(id);

		// 1. Xử lý password (super admin không cần oldPassword)
		if (StringUtils.hasText(request.getNewPassword())) {
			if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}
			user.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
		}

		// 2. Update displayName + avatar
		user.setDisplayName(request.getDisplayName());

		User updatedUser = this.userRepository.save(user);

		return modelMapper.map(updatedUser, UserDTO.class);
	}

	public void toggleEnabled(Long id) {
		User user = findById(id);

		// Đảo ngược trạng thái enable
		user.setEnabled(!user.getEnabled());

		// Nếu disable thì đổi status về LOCKED, nếu enable thì ACTIVE
		if (user.getEnabled()) {
			user.setStatus(UserStatus.ACTIVE);
			user.setOtp(null);
			user.setOtpExpiredAt(null);
			if (user.getEmailVerifiedAt() == null) {
				user.setEmailVerifiedAt(LocalDateTime.now());
			}
		} else {
			user.setStatus(UserStatus.LOCKED);
		}

		userRepository.save(user);
	}

	public void delete(Long id) {
		User user = findById(id);

		user.setIsDeleted(true);
		user.setDeletedAt(LocalDateTime.now());
		user.setEnabled(false);
		user.setStatus(UserStatus.LOCKED);

		this.userRepository.save(user);
	}
}
