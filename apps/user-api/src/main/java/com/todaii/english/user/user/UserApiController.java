package com.todaii.english.user.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/v1")
public class UserApiController {
	@GetMapping
	public String temp() {
		return "hello world";
	}
}
