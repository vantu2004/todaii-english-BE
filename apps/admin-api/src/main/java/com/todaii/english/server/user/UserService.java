package com.todaii.english.server.user;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.core.service.SmtpService;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.UpdateUserRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final ModelMapper modelMapper;
  private final SmtpService smtpService;
  private final UsageStatisticPort usageStatisticPort;

  @Deprecated
  public List<UserDTO> findAll() {
    List<User> users = userRepository.findAll();

    return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
  }

  public Page<UserDTO> findAllPaged(
      int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    Page<User> userPage =
        userRepository.findAllActive(AdminUtils.formatSearchKeyword(keyword), pageable);

    return userPage.map(user -> modelMapper.map(user, UserDTO.class));
  }

  public User findById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
  }

  public UserDTO findUserDTOById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    return modelMapper.map(user, UserDTO.class);
  }

  public UserDTO update(Long currentAdminId, Long id, @Valid UpdateUserRequest request) {
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

    smtpService.accountUpdatedNotice(updatedUser.getEmail(), updatedUser.getDisplayName());

    createUsageStatistic(currentAdminId);

    return modelMapper.map(updatedUser, UserDTO.class);
  }

  public void toggleEnabled(Long currentAdminId, Long id) {
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

      smtpService.accountUnBannedNotice(user.getEmail(), user.getDisplayName());
    } else {
      user.setStatus(UserStatus.LOCKED);

      smtpService.accountBannedNotice(user.getEmail(), user.getDisplayName());
    }

    createUsageStatistic(currentAdminId);

    userRepository.save(user);
  }

  public void delete(Long currentAdminId, Long id) {
    User user = findById(id);

    smtpService.accountDeletedNotice(user.getEmail(), user.getDisplayName());

    createUsageStatistic(currentAdminId);

    user.setIsDeleted(true);
    user.setDeletedAt(LocalDateTime.now());
    user.setEnabled(false);
    user.setStatus(UserStatus.LOCKED);

    this.userRepository.save(user);
  }

  private void createUsageStatistic(Long currentAdminId) {
    UsageStatistic mailSendStatistic =
        usageStatisticPort.createMailSendStatistic(currentAdminId, ActorType.ADMIN);
    usageStatisticPort.createUsageStatistic(mailSendStatistic);
  }
}
