package com.todaii.english.shared.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardChartDTO {
	private Map<String, Long> logSummary; // eventType -> tổng quantity
	private Map<String, List<ChartPoint>> logTrends; // eventType -> trend theo ngày

	private TokenSummary aiTokenSummary; // AI_REQUEST -> tổng in/out token
	private Map<String, TokenChartPoint> aiTokenTrends; // AI_REQUEST -> trend theo ngày

	@Getter
	@Setter
	@AllArgsConstructor
	public static class ChartPoint {
		private String date;
		private Long quantity = 0L;
	}

	@Getter
	@Setter
	public static class TokenSummary {
		private Long inputToken = 0L;
		private Long outputToken = 0L;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class TokenChartPoint {
		private Long inputToken = 0L;
		private Long outputToken = 0L;
	}
}
