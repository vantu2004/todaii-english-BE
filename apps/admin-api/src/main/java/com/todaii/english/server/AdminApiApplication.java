package com.todaii.english.server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.todaii.english" })
public class AdminApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(AdminApiApplication.class, args);
  }
}
