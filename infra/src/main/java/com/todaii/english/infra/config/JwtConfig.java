package com.todaii.english.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.todaii.english.infra.security.jwt.JwtProperties;

// đóng vai trò như class JpaConfig giúp Spring quét qua các class cấu hình (JwtProperties)
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
	
}
