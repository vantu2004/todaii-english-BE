package com.todaii.english.infra.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.core.port.YoutubeDataApiV3Port;
import com.todaii.english.shared.constants.ApiUrl;
import com.todaii.english.shared.constants.SettingKey;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.YoutubeSearchResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class YoutubeDataApiV3Client implements YoutubeDataApiV3Port {
	private final RestTemplate restTemplate;
	private final SettingQueryPort settingQueryPort;

	public YoutubeDataApiV3Client(SettingQueryPort settingQueryPort) {
		this.settingQueryPort = settingQueryPort;
		this.restTemplate = new RestTemplate();
	}

	@Override
	public YoutubeSearchResponse fetchFromYoutube(String keyword, String type, int size) {
		String url = buildUrl(keyword, type, size);
		try {
			ResponseEntity<YoutubeSearchResponse> response = restTemplate.getForEntity(url,
					YoutubeSearchResponse.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Failed to fetch data from YouTube API: {}", e.getMessage(), e);
			throw e;
		}
	}

	private String buildUrl(String keyword, String type, int size) {
		String apiKey = settingQueryPort.getSettingByKey(SettingKey.YOUTUBE_API_KEY).getValue();

		switch (type.toLowerCase()) {
		case "video":
			return String.format(ApiUrl.YOUTUBE_DATA_API_V3_VIDEO, keyword, size, apiKey);
		case "playlist":
			return String.format(ApiUrl.YOUTUBE_DATA_API_V3_PLAYLIST, keyword, size, apiKey);
		default:
			throw new BusinessException(404, "Unknow type");
		}
	}
}
