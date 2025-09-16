package com.todaii.english.admin.controller;
import com.todaii.english.shared.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/v1")
public class HelloAdminController {
  @GetMapping("/hello")
  public ApiResponse<String> hello() {
    return ApiResponse.ok("Hello Admin API! Welcome to New World.");
  }
}
