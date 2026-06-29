package com.todaii.english.server.cronjob;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
