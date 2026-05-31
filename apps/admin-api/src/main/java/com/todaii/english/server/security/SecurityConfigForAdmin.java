package com.todaii.english.server.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.todaii.english.infra.security.jwt.JwtAuthEntryPoint;
import com.todaii.english.infra.security.jwt.JwtTokenFilter;
import com.todaii.english.shared.constants.AdminAuthorities;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
// log list filter
@EnableWebSecurity(debug = true)
public class SecurityConfigForAdmin {
  private final AdminCookieAuthFilter adminCookieAuthFilter;
  private final JwtTokenFilter jwtTokenFilter;
  private final JwtAuthEntryPoint jwtAuthEntryPoint;

  // tự tìm trên biến môi trường của cloud trước, ko có thì mới tới local
  @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:5174}")
  private String[] allowedOrigins;

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
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
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
  SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(this::configureCors)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthEntryPoint))
        .authorizeHttpRequests(this::configureAuthorizations)

        /*
         * nhờ chế độ debug của @EnableWebSecurity(debug = true), ta thấy
         * AuthorizationFilter nằm cuối -> add jwtTokenFilter để check token trc khi xác
         * thực bởi AuthorizationFilter
         */
        /*
         * thêm filter xử lý token từ cookie lấy từ request trước khi thêm filter thực
         * hiện decode token phía dưới
         */
        .addFilterBefore(adminCookieAuthFilter, AuthorizationFilter.class)
        .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class);

    return http.build();
  }

  private void configureCors(
      org.springframework.security.config.annotation.web.configurers.CorsConfigurer<HttpSecurity>
          cors) {
    cors.configurationSource(
        request -> {
          var config = new org.springframework.web.cors.CorsConfiguration();
          config.setAllowedOriginPatterns(List.of(allowedOrigins));
          config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
          config.setAllowedHeaders(List.of("*"));
          config.setExposedHeaders(List.of("*"));
          config.setAllowCredentials(true);
          return config;
        });
  }

  private void configureAuthorizations(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          auth) {
    auth
        // 1. Công khai
        .requestMatchers("/api/v1/auth/**")
        .permitAll()

        // 2. Dashboard
        .requestMatchers("/api/v1/dashboard/**")
        .hasAnyAuthority(AdminAuthorities.ALL_ADMINS)

        // 3. Quản lý Admin & Settings (Hầu hết là Super Admin)
        .requestMatchers(HttpMethod.GET, "/api/v1/admin/me")
        .hasAnyAuthority(AdminAuthorities.ALL_ADMINS)
        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/me")
        .hasAnyAuthority(AdminAuthorities.ALL_ADMINS)

        // 4. Quản lý User
        .requestMatchers("/api/v1/user/**")
        .hasAnyAuthority(AdminAuthorities.SUPER_AND_USER)

        // 5. Quản lý Nội dung (Topic, Dictionary, Video, Article, Vocab, TOEIC)
        .requestMatchers(
            "/api/v1/topic/**",
            "/api/v1/dictionary/**",
            "/api/v1/video/**",
            "/api/v1/vocab-group/**",
            "/api/v1/vocab-deck/**",
            "/api/v1/article/**",
            "/api/v1/toeic/**",
            "/api/v1/cloudinary/file/**",
            "/api/v1/tts/**")
        .hasAnyAuthority(AdminAuthorities.SUPER_AND_CONTENT)

        // Tất cả các request khác phải authenticated
        .anyRequest()
        .authenticated();
  }
}
