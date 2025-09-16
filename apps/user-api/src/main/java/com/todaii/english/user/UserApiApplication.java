package com.todaii.english.user;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.todaii.english")
public class UserApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(UserApiApplication.class, args);
  }
}
