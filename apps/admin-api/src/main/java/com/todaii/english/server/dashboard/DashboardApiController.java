package com.todaii.english.server.dashboard;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<DashboardSummaryDTO> getSummary() {
		return ResponseEntity.ok(dashboardService.getSummary());
	}

	@GetMapping("/admin-chart")
	public ResponseEntity<DashboardChartDTO> getChart(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {

		LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
		LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

		DashboardChartDTO dto = dashboardService.getAdminDashboardChart(start, end);

		return ResponseEntity.ok(dto);
	}

}
