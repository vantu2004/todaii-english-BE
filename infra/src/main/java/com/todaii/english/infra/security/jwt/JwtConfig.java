package com.todaii.english.infra.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// đóng vai trò như class JpaConfig giúp Spring quét qua các class cấu hình (JwtProperties)
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
	
}
