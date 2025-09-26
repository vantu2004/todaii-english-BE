package com.todaii.english.infra.security.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminRepository;

@Service
public class CustomAdminDetailsService implements UserDetailsService {

	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Admin> user = this.adminRepository.findByEmail(email);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("No user found with the given user name!");
		}

		return new CustomAdminDetails(user.get());
	}

}
