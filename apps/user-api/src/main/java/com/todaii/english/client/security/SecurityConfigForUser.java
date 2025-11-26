package com.todaii.english.client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
// log list filter
@EnableWebSecurity(debug = true)
public class SecurityConfigForUser {
	private final UserCookieAuthFilter userCookieAuthFilter;
	private final JwtTokenFilter jwtTokenFilter;
	private final JwtAuthEntryPoint jwtAuthEntryPoint;

	@Bean
	UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * AuthenticationManager này được cấu hình dựa trên các cấu hình bảo mật đã khai
	 * báo trước đó (ví dụ: UserDetailsService, PasswordEncoder, v.v.), có
	 * thể @Autowired AuthenticationManager ở bất cứ đâu cần xác thực người dùng, ví
	 * dụ: controller xử lý đăng nhập, custom filter xác thực JWT.
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/*
	 * thay vì để cấu hình mặc định thì tự cấu hình lại AuthenticationProvider, tạo
	 * và trả về một đối tượng DaoAuthenticationProvider chứa thông tin user đã đc
	 * xác thực
	 */
	@Bean
	AuthenticationProvider authProviderForUser() {
		// là lớp triển khai của AuthenticationProvider dùng xác thực user
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userDetailsService());
		provider.setPasswordEncoder(this.passwordEncoder());

		return provider;
	}

	@Bean
	SecurityFilterChain UserSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(request -> {
			var corsConfig = new org.springframework.web.cors.CorsConfiguration();
			corsConfig.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
			corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
			corsConfig.setAllowedHeaders(java.util.List.of("*"));
			corsConfig.setAllowCredentials(true);
			return corsConfig;
		}))

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
				.authorizeHttpRequests(auth -> auth
						// AuthApiController
						.requestMatchers("/api/v1/auth/**").permitAll()

						// ArticleApiController
						.requestMatchers("/api/v1/article/saved").hasAuthority("USER")
						.requestMatchers("/api/v1/article/*/is-saved").hasAuthority("USER")
						.requestMatchers("/api/v1/article/**").permitAll()

						// UserApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/user/me").hasAuthority("USER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/user/me").hasAuthority("USER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/user/article/*").hasAuthority("USER")

						// TopicApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/topic/**").permitAll()

						// VideoApiController
						.requestMatchers("/api/v1/video/saved").hasAuthority("USER")
						.requestMatchers("/api/v1/video/*/is-saved").hasAuthority("USER")
						.requestMatchers("/api/v1/video/**").permitAll()

						// AuthApiController
						.requestMatchers("/api/v1/dictionary/**").permitAll()

						// NotebookApiController + NoteDictApiController
						.requestMatchers("/api/v1/notebook/**").hasAuthority("USER")

						.anyRequest().authenticated());

		/*
		 * nhờ chế độ debug của @EnableWebSecurity(debug = true), ta thấy
		 * AuthorizationFilter nằm cuối -> add jwtTokenFilter để check token trc khi xác
		 * thực bởi AuthorizationFilter
		 */
		/*
		 * thêm filter xử lý token từ cookie lấy từ request trước khi thêm filter thực
		 * hiện decode token phía dưới
		 */
		httpSecurity.addFilterBefore(userCookieAuthFilter, AuthorizationFilter.class);

		httpSecurity.addFilterBefore(this.jwtTokenFilter, AuthorizationFilter.class);

		return httpSecurity.build();
	}
}
