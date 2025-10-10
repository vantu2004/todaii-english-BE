package com.todaii.english.server.video;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Topic;
import com.todaii.english.core.entity.Video;
import com.todaii.english.server.topic.TopicRepository;
import com.todaii.english.shared.constants.YoutubeOEmbed;
import com.todaii.english.shared.dto.VideoDTO;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
	private final VideoRepository videoRepository;
	private final VideoLyricLineRepository videoLyricLineRepository;
	private final ObjectMapper objectMapper;
	private final ModelMapper modelMapper;
	private final TopicRepository topicRepository;

	public VideoDTO importFromYoutube(String youtubeUrl) throws BadRequestException {
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

			VideoDTO videoDTO = VideoDTO.builder().title(title).authorName(authorName).providerName(providerName)
					.providerUrl(providerUrl).thumbnailUrl(thumbnailUrl).embedHtml(embedHtml).videoUrl(youtubeUrl)
					.cefrLevel(CefrLevel.A1).topicIds(null).build();

			return videoDTO;

		} catch (HttpClientErrorException e) {
			throw new BusinessException(e.getStatusCode().value(), e.getStatusText());
		} catch (Exception e) {
			// Bắt mọi lỗi còn lại (JSON parse, network, v.v.)
			throw new RuntimeException("Failed to import YouTube video: " + e.getMessage(), e);
		}
	}

	public Video findById(Long id) {
		return videoRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Video not found"));
	}

	public List<Video> findAll() {
		return videoRepository.findAll();
	}

	public Video createVideo(VideoDTO videoDTO) {
		if (videoRepository.existsByVideoUrl(videoDTO.getVideoUrl())) {
			throw new BusinessException(409, "Video already exists with this URL.");
		}

		Video video = modelMapper.map(videoDTO, Video.class);

		Set<Topic> topics = new HashSet<>(topicRepository.findAllById(videoDTO.getTopicIds()));
		if (topics.size() != videoDTO.getTopicIds().size()) {
			throw new BusinessException(404, "One or more topics not found");
		}

		video.setTopics(topics);
		return videoRepository.save(video);
	}

	public Video updateVideo(Long id, VideoDTO videoDTO) {
		Video existingVideo = findById(id);
		if (videoRepository.existsByVideoUrl(videoDTO.getVideoUrl())
				&& !existingVideo.getVideoUrl().equals(videoDTO.getVideoUrl())) {
			throw new BusinessException(409, "Another video already exists with this URL.");
		}

		modelMapper.map(videoDTO, existingVideo);

		Set<Topic> topics = new HashSet<>(topicRepository.findAllById(videoDTO.getTopicIds()));
		if (topics.size() != videoDTO.getTopicIds().size()) {
			throw new BusinessException(404, "One or more topics not found");
		}
		existingVideo.setTopics(topics);

		return videoRepository.save(existingVideo);
	}

	public void toggleEnabled(Long id) {
		Video video = findById(id);
		video.setEnabled(!video.getEnabled());

		videoRepository.save(video);
	}

	@Transactional
	public void deleteVideo(Long id) {
		videoLyricLineRepository.deleteAllByVideoId(id);
		videoRepository.deleteById(id);
	}

}
