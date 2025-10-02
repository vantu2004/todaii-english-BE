package com.todaii.english.infra.security.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.todaii.english.core.client.user.UserRepository;
import com.todaii.english.core.entity.User;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = this.userRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return new CustomUserDetails(user);
	}

}
