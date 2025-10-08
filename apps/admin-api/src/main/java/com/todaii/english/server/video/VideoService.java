package com.todaii.english.server.video;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.constants.YoutubeOEmbed;
import com.todaii.english.shared.dto.VideoDTO;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
	private final VideoRepository videoRepository;
	private final ObjectMapper objectMapper;
	private final ModelMapper modelMapper;

	public Video importFromYoutube(String youtubeUrl) throws BadRequestException {
		String requestUri = YoutubeOEmbed.BASE_URL.replace("<URL>", youtubeUrl).replace("<FORMAT>", "json");

		try {
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.getForObject(requestUri, String.class);

			JsonNode json = objectMapper.readTree(response);

			String title = json.path("title").asText();
			String authorName = json.path("author_name").asText();
			String providerName = json.path("provider_name").asText();
			String providerUrl = json.path("provider_url").asText();
			String thumbnailUrl = json.path("thumbnail_url").asText();
			String embedHtml = json.path("html").asText().replace("width=\"200\"", "width=\"100%\"")
					.replace("height=\"113\"", "height=\"100%\"");

			Video video = Video.builder().title(title).authorName(authorName).providerName(providerName)
					.providerUrl(providerUrl).thumbnailUrl(thumbnailUrl).embedHtml(embedHtml).videoUrl(youtubeUrl)
					.views(0).cefrLevel(CefrLevel.A1).build();

			return video;

		} catch (HttpClientErrorException e) {
			throw new BusinessException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			// Bắt mọi lỗi còn lại (JSON parse, network, v.v.)
			throw new RuntimeException("Failed to import YouTube video: " + e.getMessage(), e);
		}
	}

//	public Video createVideo(VideoDTO videoDTO) {
//		if (videoRepository.existsByVideoUrl(videoDTO.getVideoUrl())) {
//			throw new BusinessException(409, "Video already exists with this URL.");
//		}
//
//		Video video = modelMapper.map(videoDTO, Video.class);
//
//		return video;
//	}
}
