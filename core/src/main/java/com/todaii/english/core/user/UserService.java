package com.todaii.english.core.user;

import com.todaii.english.shared.enums.UserStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder; // để hash/verify mật khẩu
//
//    /** Đăng ký user mới */
//    public User registerUser(String email, String rawPassword, String displayName) {
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new IllegalArgumentException("Email đã tồn tại");
//        }
//
//        User user = User.builder()
//                .email(email)
//                .passwordHash(passwordEncoder.encode(rawPassword))
//                .displayName(displayName)
//                .status(UserStatus.PENDING)
//                .build();
//
//        return userRepository.save(user);
//    }
//
//    /** Xác minh email */
//    public User verifyEmail(Long userId) {
//        return userRepository.findById(userId).map(u -> {
//            u.setEmailVerifiedAt(LocalDateTime.now());
//            u.setStatus(UserStatus.ACTIVE);
//            return userRepository.save(u);
//        }).orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
//    }
//
//    /** Đăng nhập bằng email/password */
//    public Optional<User> loginWithPassword(String email, String rawPassword) {
//        return userRepository.findByEmail(email)
//                .filter(u -> !u.getIsDeleted() && u.getStatus() == UserStatus.ACTIVE)
//                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
//                .map(u -> {
//                    u.setLastLoginAt(LocalDateTime.now());
//                    return userRepository.save(u);
//                });
//    }
//
//    /** Đăng nhập bằng Google Sub */
//    public Optional<User> loginWithGoogle(String googleSub) {
//        return userRepository.findByGoogleSub(googleSub)
//                .filter(u -> !u.getIsDeleted() && u.getStatus() == UserStatus.ACTIVE)
//                .map(u -> {
//                    u.setLastLoginAt(LocalDateTime.now());
//                    return userRepository.save(u);
//                });
//    }
//
//    /** Đổi mật khẩu */
//    public void changePassword(Long userId, String newRawPassword) {
//        userRepository.findById(userId).ifPresent(u -> {
//            u.setPasswordHash(passwordEncoder.encode(newRawPassword));
//            userRepository.save(u);
//        });
//    }
//
//    /** Cập nhật profile */
//    public User updateProfile(Long userId, String displayName, String avatarUrl) {
//        return userRepository.findById(userId).map(u -> {
//            u.setDisplayName(displayName);
//            u.setAvatarUrl(avatarUrl);
//            return userRepository.save(u);
//        }).orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
//    }
//
//    /** Soft delete */
//    public void deleteUser(Long userId) {
//        userRepository.findById(userId).ifPresent(u -> {
//            u.setIsDeleted(true);
//            u.setDeletedAt(LocalDateTime.now());
//            userRepository.save(u);
//        });
//    }
//
//    // ===== CRUD cơ bản nếu cần =====
//    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }
//    public List<User> getAllUsers() { return userRepository.findAll(); }
}
