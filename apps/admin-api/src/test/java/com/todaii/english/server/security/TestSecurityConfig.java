package com.todaii.english.server.security;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.todaii.english.infra.security.jwt.JwtAuthEntryPoint;
import com.todaii.english.infra.security.jwt.JwtUtility;

@TestConfiguration
public class TestSecurityConfig {

	/*
	 * Khi chạy test với @WebMvcTest, Spring không load full app, nhưng nó vẫn scan
	 * tất cả @Component/@Configuration trong package liên quan đến controller (trừ
	 * khi exclude).
	 * 
	 * JwtTokenFilter nằm trong infra.security.jwt có @Component/@Bean. Spring vẫn
	 * khởi tạo JwtTokenFilter → mà JwtTokenFilter constructor yêu cầu JwtUtility.
	 * Cho dù có permitAll() trong TestSecurityConfig, Spring vẫn cố gắng tạo bean
	 * JwtTokenFilter trước khi áp rule đó.
	 */
	@Bean
	public JwtUtility jwtUtility() {
		return Mockito.mock(JwtUtility.class);
	}

	@Bean
	public JwtAuthEntryPoint jwtAuthEntryPoint() {
		return new JwtAuthEntryPoint(); // hoặc Mockito.mock(JwtAuthEntryPoint.class) nếu chỉ cần dummy
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint())) // <<< rất quan trọng
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/v1/admin/me")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER", "USER_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/admin/me")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER", "USER_MANAGER").anyRequest().permitAll());
		return http.build();
	}

}
