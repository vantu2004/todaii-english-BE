package com.todaii.english.infra.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/*
 * nếu dùng @Configuration trong này thì class này mang tính chất cấu hình và
 * mất đi tính data holder -> nhờ JwtConfig kích hoạt giùm
 */
// tìm các property trong file cấu hình và ánh xạ vào 2 biến
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtProperties {
	private String issuer;
	private String secret;
}
