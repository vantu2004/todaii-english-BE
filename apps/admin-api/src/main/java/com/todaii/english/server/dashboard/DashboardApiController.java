package com.todaii.english.server.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.response.DashboardChartDTO;
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

  @GetMapping("/admin-chart")
  public ResponseEntity<List<DashboardChartDTO>> getAdminChart(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    return ResponseEntity.ok(
        dashboardService.getChartByActorType(ActorType.ADMIN, startDate, endDate));
  }

  @GetMapping("/admin-chart/{id}")
  public ResponseEntity<List<DashboardChartDTO>> getSpecificAdminChart(
      @PathVariable Long id,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    return ResponseEntity.ok(
        dashboardService.getChartByActorId(ActorType.ADMIN, id, startDate, endDate));
  }

  @GetMapping("/user-chart")
  public ResponseEntity<List<DashboardChartDTO>> getUserChart(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    return ResponseEntity.ok(
        dashboardService.getChartByActorType(ActorType.USER, startDate, endDate));
  }

  @GetMapping("/user-chart/{id}")
  public ResponseEntity<List<DashboardChartDTO>> getSpecificUserChart(
      @PathVariable Long id,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    return ResponseEntity.ok(
        dashboardService.getChartByActorId(ActorType.USER, id, startDate, endDate));
  }

  @GetMapping("/guest-chart")
  public ResponseEntity<List<DashboardChartDTO>> getGuestChart(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    return ResponseEntity.ok(
        dashboardService.getChartByActorType(ActorType.GUEST, startDate, endDate));
  }

  @GetMapping("/my-chart")
  public ResponseEntity<List<DashboardChartDTO>> getMyChart(
      Authentication authentication,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {

    startDate = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
    endDate = (endDate != null) ? endDate : LocalDate.now();

    Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

    return ResponseEntity.ok(
        dashboardService.getChartByActorId(ActorType.ADMIN, currentAdminId, startDate, endDate));
  }
}
