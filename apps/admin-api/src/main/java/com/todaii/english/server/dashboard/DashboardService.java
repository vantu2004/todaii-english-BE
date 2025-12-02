package com.todaii.english.server.dashboard;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.AdminEvent;
import com.todaii.english.core.entity.BaseEvent;
import com.todaii.english.core.entity.UserEvent;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.dictionary.DictionaryEntryRepository;
import com.todaii.english.server.event.AdminEventRepository;
import com.todaii.english.server.event.UserEventRepository;
import com.todaii.english.server.video.VideoRepository;
import com.todaii.english.server.vocabulary.VocabDeckRepository;
import com.todaii.english.shared.enums.EventType;
import com.todaii.english.shared.response.DashboardChartDTO;
import com.todaii.english.shared.response.DashboardChartDTO.ChartPoint;
import com.todaii.english.shared.response.DashboardChartDTO.TokenChartPoint;
import com.todaii.english.shared.response.DashboardChartDTO.TokenSummary;
import com.todaii.english.shared.response.DashboardSummaryDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	private final ArticleRepository articleRepository;
	private final VideoRepository videoRepository;
	private final DictionaryEntryRepository dictionaryEntryRepository;
	private final VocabDeckRepository vocabDeckRepository;
	private final AdminEventRepository adminEventRepository;
	private final UserEventRepository userEventRepository;
	private final ObjectMapper objectMapper;

	public DashboardSummaryDTO getSummary() {
		return new DashboardSummaryDTO(articleRepository.count(), videoRepository.count(), vocabDeckRepository.count(),
				dictionaryEntryRepository.count());
	}

	public DashboardChartDTO getAdminDashboardChart(LocalDate start, LocalDate end) {
		List<AdminEvent> logs = adminEventRepository.findByCreatedAtBetween(start.atStartOfDay(),
				end.atTime(23, 59, 59));

		return buildDashboardChart(logs);
	}

	public DashboardChartDTO getUserDashboardChart(LocalDate start, LocalDate end) {
		List<UserEvent> logs = userEventRepository.findByCreatedAtBetween(start.atStartOfDay(), end.atTime(23, 59, 59));

		return buildDashboardChart(logs);
	}

	// GENERIC: dùng chung cho cả Admin và User
	private <T extends BaseEvent> DashboardChartDTO buildDashboardChart(List<T> logs) {
		Map<String, Long> logSummary = buildLogSummary(logs);
		Map<String, List<ChartPoint>> logTrends = buildLogTrends(logs);

		// AI only
		List<T> aiEvents = logs.stream().filter(e -> e.getEventType() == EventType.AI_REQUEST).toList();

		TokenSummary aiTokenSummary = buildAiTokenSummary(aiEvents);
		Map<String, List<TokenChartPoint>> aiTokenTrends = buildAiTokenTrends(aiEvents);

		return new DashboardChartDTO(logSummary, logTrends, aiTokenSummary, aiTokenTrends);
	}

	// tính tổng số liệu các eventType
	private <T extends BaseEvent> Map<String, Long> buildLogSummary(List<T> logs) {
		return logs.stream().collect(
				Collectors.groupingBy(e -> e.getEventType().name(), Collectors.summingLong(BaseEvent::getQuantity)));
	}

	// thống kê số liệu theo ngày các eventType
	private <T extends BaseEvent> Map<String, List<ChartPoint>> buildLogTrends(List<T> logs) {

		return logs.stream()
				.collect(Collectors.groupingBy(e -> e.getEventType().name(),
						Collectors.groupingBy(e -> e.getCreatedAt().toLocalDate().toString(),
								Collectors.summingLong(BaseEvent::getQuantity))))
				.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						e -> e.getValue().entrySet().stream().map(d -> new ChartPoint(d.getKey(), d.getValue()))
								.sorted(Comparator.comparing(ChartPoint::getDate)).toList()));
	}

	// tính tổng token
	private <T extends BaseEvent> TokenSummary buildAiTokenSummary(List<T> aiEvents) {
		return aiEvents.stream().map(e -> parseAiMetadata(e.getMetadata())).reduce(new TokenSummary(), (a, b) -> {
			a.setInputToken(a.getInputToken() + b.getInputToken());
			a.setOutputToken(a.getOutputToken() + b.getOutputToken());
			return a;
		});
	}

	// tính token theo ngày
	private <T extends BaseEvent> Map<String, List<TokenChartPoint>> buildAiTokenTrends(List<T> aiEvents) {

		return aiEvents.stream()
				.collect(Collectors.groupingBy(e -> e.getCreatedAt().toLocalDate().toString(),
						Collectors.mapping(e -> parseAiMetadata(e.getMetadata()), Collectors.toList())))
				.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
					String date = entry.getKey();
					long totalIn = entry.getValue().stream().mapToLong(TokenSummary::getInputToken).sum();
					long totalOut = entry.getValue().stream().mapToLong(TokenSummary::getOutputToken).sum();
					return List.of(new TokenChartPoint(date, totalIn, totalOut));
				}));
	}

	private TokenSummary parseAiMetadata(String json) {
		try {
			return objectMapper.readValue(json, TokenSummary.class);
		} catch (Exception e) {
			return new TokenSummary();
		}
	}
}
