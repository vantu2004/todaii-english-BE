package com.todaii.english.server.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRole;

public class CustomAdminDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Admin admin;

	public CustomAdminDetails(Admin admin) {
		this.admin = admin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		/*
		 * SimpleGrantedAuthority là class kế thừa GrantedAuthority đại diện cho 1 role
		 * của 1 user, 1 user có thể có nhiều role nên dùng list để lấy hết role
		 */
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		for (AdminRole role : admin.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getCode())); // truyền code, vd: SUPER_ADMIN
		}

		return authorities;
	}

	@Override
	public String getPassword() {
		return this.admin.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return this.admin.getDisplayName();
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
		return this.admin.getEnabled();
	}

	public Admin getAdmin() {
		return this.admin;
	}

}
