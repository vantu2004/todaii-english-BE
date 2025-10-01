package com.todaii.english.infra.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.todaii.english.core.user.user.User;
import com.todaii.english.infra.security.jwt.JwtUserDetailsFactory;

import io.jsonwebtoken.Claims;

@Component("userDetailsFactory")
public class UserDetailsFactory implements JwtUserDetailsFactory {
	@Override
	public UserDetails build(Claims claims) {
		String[] subjectArr = claims.getSubject().split(",");
		Long id = Long.parseLong(subjectArr[0]);
		String displayName = subjectArr[1];

		User user = new User();
		user.setId(id);
		user.setDisplayName(displayName);

		return new CustomUserDetails(user); // implements UserDetails (authorities = empty list)
	}
}
