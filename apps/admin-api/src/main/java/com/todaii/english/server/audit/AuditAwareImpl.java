package com.todaii.english.server.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.todaii.english.core.entity.admin.Admin;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.server.admin.AdminRepository;

import lombok.RequiredArgsConstructor;

@Component("auditAwareImpl")
@RequiredArgsConstructor
public class AuditAwareImpl implements AuditorAware<Admin> {
  private final AdminRepository adminRepository;

  @Override
  public Optional<Admin> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }

    Long adminId = AdminUtils.getCurrentAdminId(authentication);

    /* findById() tìm full admin trong khi hibernate chỉ cần id để set cho admin ToeicTest =>
    getReferenceById() chỉ tạo proxy object (object giả) chỉ chứa id, khi nào cần cái khác thì
    mới query*/
    return Optional.of(adminRepository.getReferenceById(adminId));
  }
}
