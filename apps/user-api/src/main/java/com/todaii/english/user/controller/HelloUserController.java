package com.todaii.english.user.controller;
import com.todaii.english.shared.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/v1")
public class HelloUserController {
  @GetMapping("/hello")
  public ApiResponse<String> hello() {
    return ApiResponse.ok("Hello User API! Welcome to New World.");
  }
}
