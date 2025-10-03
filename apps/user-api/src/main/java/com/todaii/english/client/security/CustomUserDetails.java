package com.todaii.english.client.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.todaii.english.core.entity.User;

public class CustomUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// hardcode trả về mặc định role USER luôn
		return List.of(new SimpleGrantedAuthority("USER"));
	}

	@Override
	public String getPassword() {
		return this.user.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return this.user.getDisplayName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.getEnabled();
	}

	public User getUser() {
		return this.user;
	}

}
