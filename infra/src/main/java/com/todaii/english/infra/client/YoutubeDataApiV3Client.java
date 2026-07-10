package com.todaii.english.infra.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.todaii.english.core.port.YoutubeDataApiV3Port;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.YoutubeSearchResponse;
import com.todaii.english.shared.response.YoutubeVideoDetailsResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "youtube.api.key")
public class YoutubeDataApiV3Client implements YoutubeDataApiV3Port {
  private final RestTemplate restTemplate;
  private final String youtubeApiKey;

  public YoutubeDataApiV3Client(@Value("${youtube.api.key}") String youtubeApiKey) {
    this.restTemplate = new RestTemplate();
    this.youtubeApiKey = youtubeApiKey;
  }

  @Override
  public YoutubeSearchResponse fetchFromYoutube(String keyword, String type, int size) {
    String url = buildUrl(keyword, type, size);
    try {
      ResponseEntity<YoutubeSearchResponse> response =
          restTemplate.getForEntity(url, YoutubeSearchResponse.class);
      return response.getBody();
    } catch (Exception e) {
      log.error("Failed to fetch data from YouTube API: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public String getVideoDuration(String videoId) {
    String url = String.format(ApiUrl.YOUTUBE_DATA_API_V3_VIDEO_DETAILS, videoId, youtubeApiKey);
    try {
      ResponseEntity<YoutubeVideoDetailsResponse> response =
          restTemplate.getForEntity(url, YoutubeVideoDetailsResponse.class);
      YoutubeVideoDetailsResponse body = response.getBody();
      if (body != null && body.getItems() != null && !body.getItems().isEmpty()) {
        return body.getItems().getFirst().getContentDetails().getDuration();
      }
      throw new BusinessException(404, "YouTube video metadata items are empty");
    } catch (Exception e) {
      log.error("Failed to fetch video duration from YouTube API: {}", e.getMessage(), e);
      throw new BusinessException(400, "Failed to fetch video duration: " + e.getMessage());
    }
  }

  private String buildUrl(String keyword, String type, int size) {
    return switch (type.toLowerCase()) {
      case "video" -> String.format(ApiUrl.YOUTUBE_DATA_API_V3_VIDEO, keyword, size, youtubeApiKey);
      case "playlist" -> String.format(
          ApiUrl.YOUTUBE_DATA_API_V3_PLAYLIST, keyword, size, youtubeApiKey);
      default -> throw new BusinessException(404, "Unknow type");
    };
  }
}
