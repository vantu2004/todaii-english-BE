package com.todaii.english.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.todaii.english")
@EntityScan(basePackages = "com.todaii.english.core.entity")
public class UserApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);
	}
}
