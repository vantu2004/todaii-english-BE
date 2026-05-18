package com.todaii.english.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardSummaryDTO {
  private Long totalAdmins;
  private Long totalArticles;
  private Long totalDictionaryEntries;
  private Long totalToeicTest;
  private Long totalUsers;
  private Long totalVideos;
  private Long totalVocabularyDecks;
}
