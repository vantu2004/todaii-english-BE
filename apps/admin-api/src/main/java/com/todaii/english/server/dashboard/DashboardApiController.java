package com.todaii.english.server.dashboard;

import com.todaii.english.shared.response.DashboardChartDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.response.DashboardSummaryDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dashboard")
public class DashboardApiController {
  private final DashboardService dashboardService;

  @GetMapping("/summary")
  public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
    return ResponseEntity.ok(dashboardService.getDashboardSummary());
  }

  // TODO @GetMapping("/admin-chart")

  // TODO @GetMapping("/admin-chart/{id}")

  // TODO @GetMapping("/my-chart")

  // TODO @GetMapping("/user-chart")

  // TODO @GetMapping("/user-chart/{id}")

  // TODO @GetMapping("/guest-chart")

  // TODO @GetMapping("/top???")
}
