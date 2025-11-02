package com.todaii.english.server.security;

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
public class SecurityConfigForAdmin {
	private final JwtTokenFilter jwtTokenFilter;
	private final JwtAuthEntryPoint jwtAuthEntryPoint;

	@Bean
	UserDetailsService adminDetailsService() {
		return new CustomAdminDetailsService();
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
	AuthenticationProvider authProviderForAdmin() {
		// là lớp triển khai của AuthenticationProvider dùng xác thực user
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.adminDetailsService());
		provider.setPasswordEncoder(this.passwordEncoder());

		return provider;
	}

	@Bean
	SecurityFilterChain adminSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Cấu hình EntryPoint cho lỗi xác thực
				.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))

				// hasRole() tự thêm tiền tố ROLE_, vd read -> ROLE_read
				.authorizeHttpRequests(auth -> auth
						// AuthApiController
						.requestMatchers("/api/v1/auth/**").permitAll()

						// AdminApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/admin").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/v1/admin/me")
						.hasAnyAuthority("SUPER_ADMIN", "USER_MANAGER", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/admin/*").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/v1/admin").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/admin/me")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER", "USER_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/admin/*").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/admin/*/enabled").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/admin/*").hasAuthority("SUPER_ADMIN")

						// SettingApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/setting").hasAuthority("SUPER_ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/setting").hasAuthority("SUPER_ADMIN")

						// UserApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/user").hasAnyAuthority("SUPER_ADMIN", "USER_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/user/*")
						.hasAnyAuthority("SUPER_ADMIN", "USER_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/user/*")
						.hasAnyAuthority("SUPER_ADMIN", "USER_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/user/*/enabled")
						.hasAnyAuthority("SUPER_ADMIN", "USER_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/user/*")
						.hasAnyAuthority("SUPER_ADMIN", "USER_MANAGER")

						// TopicApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/topic")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/topic/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/topic")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/topic/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/topic/*/enabled")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/topic/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// DictionaryApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/dictionary/gemini")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/dictionary")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/dictionary/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/dictionary")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/dictionary/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/dictionary/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// VideoApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/video/fetch")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/video")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/video/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/video")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/video/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/video/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/video/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// VideoLyricLineApiController
						.requestMatchers(HttpMethod.POST, "/api/v1/video/lyric/import")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/video/*/lyric")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/video/lyric/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/video/*/lyric/batch")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/video/lyric/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/video/lyric/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/video/*/lyric")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// VocabGroupApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/vocab-group")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/vocab-group/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/vocab-group*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/vocab-group/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/vocab-group/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/vocab-group/*/enabled")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// VocabDeckApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/vocab-deck")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/vocab-deck/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/vocab-deck")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/vocab-deck/*/word/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/vocab-deck/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/vocab-deck/*/word/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/vocab-deck/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/vocab-deck/*/enabled")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/vocab-deck/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// ArticleApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/article")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/v1/article/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/article/news-api")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/article")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/article/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/article/*/enabled")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/article/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						// ArticleParagraphApiController
						.requestMatchers(HttpMethod.GET, "/api/v1/article/*/paragraph")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/article/*/paragraph")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.POST, "/api/v1/article/paragraph/translate")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/article/paragraph/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/article/paragraph/*")
						.hasAnyAuthority("SUPER_ADMIN", "CONTENT_MANAGER")

						.anyRequest().authenticated());

		/*
		 * nhờ chế độ debug của @EnableWebSecurity(debug = true), ta thấy
		 * AuthorizationFilter nằm cuối -> add jwtTokenFilter để check token trc khi xác
		 * thực bởi AuthorizationFilter
		 */
		httpSecurity.addFilterBefore(this.jwtTokenFilter, AuthorizationFilter.class);

		return httpSecurity.build();
	}

//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//		httpSecurity.csrf(csrf -> csrf.disable())
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//		return httpSecurity.build();
//	}
}
