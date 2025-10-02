package com.todaii.english.infra.security.server;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRole;
import com.todaii.english.core.server.admin.AdminRoleRepository;
import com.todaii.english.infra.security.jwt.JwtUserDetailsFactory;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component("adminDetailsFactory")
@RequiredArgsConstructor
public class AdminDetailsFactory implements JwtUserDetailsFactory {
	private final AdminRoleRepository adminRoleRepository;

	@Override
	public UserDetails build(Claims claims) {
		String[] subjectArr = claims.getSubject().split(",");
		Long id = Long.parseLong(subjectArr[0]);
		String displayName = subjectArr[1];

		String rolesClaim = claims.get("roles", String.class);
		Set<AdminRole> roles = new HashSet<>();
		if (rolesClaim != null && !rolesClaim.isBlank()) {
			for (String code : rolesClaim.split(",")) {
				roles.add(adminRoleRepository.findById(code)
						.orElseThrow(() -> new BusinessException(AdminErrorCode.ROLE_NOT_FOUND)));
			}
		}

		Admin admin = new Admin();
		admin.setId(id);
		admin.setDisplayName(displayName);
		admin.setRoles(roles);

		return new CustomAdminDetails(admin); // implements UserDetails
	}
}
