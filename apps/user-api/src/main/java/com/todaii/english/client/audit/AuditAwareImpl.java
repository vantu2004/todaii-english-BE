package com.todaii.english.client.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.user.User;

import lombok.RequiredArgsConstructor;

@Component("auditAwareImpl")
@RequiredArgsConstructor
public class AuditAwareImpl implements AuditorAware<User> {
  private final UserRepository userRepository;

  @Override
  public Optional<User> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }

    Long userId = UserUtils.getCurrentUserId(authentication);

    /* findById() tìm full admin trong khi hibernate chỉ cần id để set cho admin ToeicTest =>
    getReferenceById() chỉ tạo proxy object (object giả) chỉ chứa id, khi nào cần cái khác thì
    mới query*/
    return Optional.of(userRepository.getReferenceById(userId));
  }
}
