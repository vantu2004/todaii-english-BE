package com.todaii.english.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.server.admin.AdminRepository;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

@Service
public class CustomAdminDetailsService implements UserDetailsService {
	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Admin admin = this.adminRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));
		return new CustomAdminDetails(admin);
	}

}
