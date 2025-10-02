package com.todaii.english.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.todaii.english")
@EntityScan("com.todaii.english.core.entity")
public class AdminApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdminApiApplication.class, args);
	}
}
