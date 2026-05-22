package com.todaii.english.client.security;

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

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
// log list filter
@EnableWebSecurity(debug = true)
public class SecurityConfigForUser {
  private final UserCookieAuthFilter userCookieAuthFilter;
  private final JwtTokenFilter jwtTokenFilter;
  private final JwtAuthEntryPoint jwtAuthEntryPoint;

  // tự tìm trên biến môi trường của cloud trước, ko có thì mới tới local
  @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:5174}")
  private String[] allowedOrigins;

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
  AuthenticationProvider authProviderForUser() {
    // là lớp triển khai của AuthenticationProvider dùng xác thực user
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(this.userDetailsService());
    provider.setPasswordEncoder(this.passwordEncoder());

    return provider;
  }

  @Bean
  SecurityFilterChain UserSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .cors(this::configureCors)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Cấu hình EntryPoint cho lỗi xác thực
        .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
        .authorizeHttpRequests(this::configureAuthorizations);

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

  private void configureCors(
      org.springframework.security.config.annotation.web.configurers.CorsConfigurer<HttpSecurity>
          cors) {
    cors.configurationSource(
        request -> {
          var corsConfig = new org.springframework.web.cors.CorsConfiguration();
          corsConfig.setAllowedOriginPatterns(List.of(allowedOrigins));
          corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
          corsConfig.setAllowedHeaders(List.of("*"));
          corsConfig.setExposedHeaders(List.of("*"));
          corsConfig.setAllowCredentials(true);
          return corsConfig;
        });
  }

  private void configureAuthorizations(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          auth) {
    // Lưu ý: Thứ tự ưu tiên từ trên xuống dưới
    auth
        // 1. Công khai hoàn toàn
        .requestMatchers("/api/v1/auth/**")
        .permitAll()
        .requestMatchers("/api/v1/dictionary/**")
        .permitAll()
        .requestMatchers("/api/v1/todaii-dictionary/**")
        .permitAll()
        .requestMatchers("/api/v1/chatbot/**")
        .permitAll()
        .requestMatchers("/api/v1/gg-translate/**")
        .permitAll()
        .requestMatchers("/api/v1/topic/**")
        .permitAll()
        .requestMatchers("/api/v1/vocab-group/**")
        .permitAll()
        .requestMatchers("/api/v1/vocab-deck/**")
        .permitAll()

        // 2. Các Endpoint cần quyền USER (Phải đặt TRƯỚC các rule permitAll chung)
        .requestMatchers(
            "/api/v1/article/saved",
            "/api/v1/article/*/is-saved",
            "/api/v1/video/saved",
            "/api/v1/video/*/is-saved",
            "/api/v1/notebook/**")
        .hasAuthority("USER")

        // 3. User Profile & Actions
        .requestMatchers(HttpMethod.GET, "/api/v1/user/me")
        .hasAuthority("USER")
        .requestMatchers(HttpMethod.PUT, "/api/v1/user/me")
        .hasAuthority("USER")
        .requestMatchers(HttpMethod.PUT, "/api/v1/user/article/*")
        .hasAuthority("USER")

        // 4. Cho phép xem nội dung còn lại (Article, Video, Toeic) mà không cần login
        .requestMatchers("/api/v1/article/**")
        .permitAll()
        .requestMatchers("/api/v1/video/**")
        .permitAll()
        .requestMatchers("/api/v1/toeic/**")
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
