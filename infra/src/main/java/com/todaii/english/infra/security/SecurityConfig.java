package com.todaii.english.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.todaii.english.infra.security.admin.CustomAdminDetailsService;
import com.todaii.english.infra.security.jwt.JwtTokenFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtTokenFilter jwtTokenFilter;

	@Bean
	public UserDetailsService adminDetailsService() {
		return new CustomAdminDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * thay vì để cấu hình mặc định thì tự cấu hình lại AuthenticationProvider, tạo
	 * và trả về một đối tượng DaoAuthenticationProvider chứa thông tin user đã đc
	 * xác thực
	 */
	@Bean
	public AuthenticationProvider authProvider() {
		// là lớp triển khai của AuthenticationProvider dùng xác thực user
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.adminDetailsService());
		provider.setPasswordEncoder(this.passwordEncoder());

		return provider;
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

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// Cấu hình EntryPoint cho lỗi xác thực
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**").permitAll()
						// hasRole() tự thêm tiền tố ROLE_, vd read -> ROLE_read
						.requestMatchers(HttpMethod.GET, "/api/v1/admin").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/admin").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/admin")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER", "USER_MANAGER")
						.requestMatchers(HttpMethod.DELETE).hasAuthority("SUPER_ADMIN").anyRequest().authenticated());

		/*
		 * nhờ chế độ debug của @EnableWebSecurity(debug = true), ta thấy
		 * AuthorizationFilter nằm cuối -> add jwtTokenFilter để check token trc khi xác
		 * thực bởi AuthorizationFilter
		 */
		httpSecurity.addFilterBefore(this.jwtTokenFilter, AuthorizationFilter.class);

		return httpSecurity.build();
	}
}
