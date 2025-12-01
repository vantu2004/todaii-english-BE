package com.todaii.english.server.dashboard;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.AdminEvent;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.dictionary.DictionaryEntryRepository;
import com.todaii.english.server.event.EventRepository;
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
	private final EventRepository eventRepository;
	private final ObjectMapper objectMapper;

	// lấy số liệu chung nhất
	public DashboardSummaryDTO getSummary() {
		return new DashboardSummaryDTO(articleRepository.count(), videoRepository.count(),
				dictionaryEntryRepository.count(), vocabDeckRepository.count());
	}

	// phục vụ vẽ chart theo ngày
	public DashboardChartDTO getAdminDashboardChart(LocalDate startDate, LocalDate endDate) {
		List<AdminEvent> logs = eventRepository.findByCreatedAtBetween(startDate.atStartOfDay(),
				endDate.atTime(23, 59, 59));

		// tất cả eventType
		Map<String, Long> logSummary = buildLogSummary(logs);
		Map<String, List<ChartPoint>> logTrends = buildLogTrends(logs);

		// Tách riêng AI_REQUEST
		List<AdminEvent> aiEvents = logs.stream().filter(e -> e.getEventType() == EventType.AI_REQUEST).toList();

		TokenSummary aiTokenSummary = buildAiTokenSummary(aiEvents);

		Map<String, List<TokenChartPoint>> aiTokenTrends = buildAiTokenTrends(aiEvents);

		return new DashboardChartDTO(logSummary, logTrends, aiTokenSummary, aiTokenTrends);
	}

	private Map<String, Long> buildLogSummary(List<AdminEvent> logs) {
		return logs.stream().collect(
				Collectors.groupingBy(e -> e.getEventType().name(), Collectors.summingLong(AdminEvent::getQuantity)));
	}

	private Map<String, List<ChartPoint>> buildLogTrends(List<AdminEvent> logs) {
		return logs.stream()
				.collect(Collectors.groupingBy(e -> e.getEventType().name(),
						Collectors.groupingBy(e -> e.getCreatedAt().toLocalDate().toString(),
								Collectors.summingLong(AdminEvent::getQuantity))))
				
				.entrySet().stream()
				
				.collect(Collectors.toMap(Map.Entry::getKey,
						e -> e.getValue().entrySet().stream().map(d -> new ChartPoint(d.getKey(), d.getValue()))
								.sorted(Comparator.comparing(ChartPoint::getDate)).toList()));
	}

	private TokenSummary buildAiTokenSummary(List<AdminEvent> aiEvents) {
		return aiEvents.stream().map(e -> parseAiMetadata(e.getMetadata())).reduce(new TokenSummary(), (a, b) -> {
			a.setInputToken(a.getInputToken() + b.getInputToken());
			a.setOutputToken(a.getOutputToken() + b.getOutputToken());

			return a;
		});
	}

	private Map<String, List<TokenChartPoint>> buildAiTokenTrends(List<AdminEvent> aiEvents) {
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
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new TokenSummary();
		}
	}
}
