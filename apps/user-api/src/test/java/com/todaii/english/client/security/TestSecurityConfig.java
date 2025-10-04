package com.todaii.english.client.security;

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

    @Bean
    JwtUtility jwtUtility() {
		return Mockito.mock(JwtUtility.class);
	}

    @Bean
    JwtAuthEntryPoint jwtAuthEntryPoint() {
		return new JwtAuthEntryPoint(); // hoặc Mockito.mock(JwtAuthEntryPoint.class) nếu chỉ cần dummy
	}

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint())) // <<< rất quan trọng
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/v1/user/me")
						.hasAnyAuthority("USER").anyRequest().permitAll());
		return http.build();
	}
}
