package com.todaii.english.client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.todaii.english.infra.security.jwt.JwtAuthEntryPoint;
import com.todaii.english.infra.security.jwt.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigForUser {
	private final JwtTokenFilter jwtTokenFilter;
	private final JwtAuthEntryPoint jwtAuthEntryPoint;

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * AuthenticationManager này được cấu hình dựa trên các cấu hình bảo mật đã khai
	 * báo trước đó (ví dụ: UserDetailsService, PasswordEncoder, v.v.), có
	 * thể @Autowired AuthenticationManager ở bất cứ đâu cần xác thực người dùng, ví
	 * dụ: controller xử lý đăng nhập, custom filter xác thực JWT.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/*
	 * thay vì để cấu hình mặc định thì tự cấu hình lại AuthenticationProvider, tạo
	 * và trả về một đối tượng DaoAuthenticationProvider chứa thông tin user đã đc
	 * xác thực
	 */
	@Bean
	public AuthenticationProvider authProviderForUser() {
		// là lớp triển khai của AuthenticationProvider dùng xác thực user
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userDetailsService());
		provider.setPasswordEncoder(this.passwordEncoder());

		return provider;
	}

	@Bean
	public SecurityFilterChain UserSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
				.authorizeHttpRequests(auth -> auth
						// AuthApiController
						.requestMatchers("/api/v1/auth/**").permitAll()

						// UserApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/user/me").hasAuthority("USER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/user/me").hasAuthority("USER").anyRequest()
						.authenticated());

		/*
		 * nhờ chế độ debug của @EnableWebSecurity(debug = true), ta thấy
		 * AuthorizationFilter nằm cuối -> add jwtTokenFilter để check token trc khi xác
		 * thực bởi AuthorizationFilter
		 */
		httpSecurity.addFilterBefore(this.jwtTokenFilter, AuthorizationFilter.class);

		return httpSecurity.build();
	}
}
