package com.todaii.english.client.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.service.DashboardService;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.response.DashboardChartDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dashboard")
public class DashboardApiController {
  private final DashboardService dashboardService;

  @GetMapping("/my-chart")
  public ResponseEntity<List<DashboardChartDTO>> getMyChart(
      Authentication authentication,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    Long currentUserId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(
        dashboardService.getChartByActorId(ActorType.USER, currentUserId, startDate, endDate));
  }
}
