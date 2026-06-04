package com.todaii.english.client.cronjob;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cronjob")
public class CronjobApiController {
  @GetMapping
  public ResponseEntity<String> sayHi() {
    return ResponseEntity.ok("Hello world!");
  }
}
