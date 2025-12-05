package com.todaii.english.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardSummaryDTO {
	private Long adminCount;
	private Long userCount;
	private Long articleCount;
	private Long videoCount;
	private Long vocabDeckCount;
	private Long dictionaryWordCount;
}
