package com.todaii.english.server.video;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Video;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.dto.VideoDTO;
import com.todaii.english.shared.response.PagedResponse;
import com.todaii.english.shared.response.YoutubeSearchResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/video")
public class VideoApiController {
	private final VideoService videoService;

	@GetMapping("/youtube")
	public ResponseEntity<VideoDTO> importFromYoutube(
			@RequestParam @NotBlank(message = "URL must not be blank") @Length(max = 1024, message = "URL must not exceed 1024 characters") String url)
			throws BadRequestException {
		return ResponseEntity.ok(videoService.importFromYoutube(url));
	}

	@GetMapping("/youtube-data-api-v3")
	public ResponseEntity<YoutubeSearchResponse> getYoutubeSearchResponse(Authentication authentication,
			@RequestParam(defaultValue = "listening english") @NotBlank(message = "Keyword must not be blank") @Length(max = 191, message = "Keyword must not exceed 191 characters") String keyword,
			@RequestParam(defaultValue = "video") @NotBlank(message = "Type must not be blank") String type,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);
		
		return ResponseEntity.ok(videoService.getYoutubeSearchResponse(currentAdminId, keyword, type, size));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Video> getVideo(@PathVariable Long id) {
		return ResponseEntity.ok(videoService.findById(id));
	}

	@Deprecated
	public ResponseEntity<List<Video>> getAllVideos() {
		List<Video> videos = videoService.findAll();
		if (videos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(videos);
	}

	@GetMapping
	public ResponseEntity<PagedResponse<Video>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Video> videos = videoService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<Video> response = new PagedResponse<>(videos.getContent(), page, size, videos.getTotalElements(),
				videos.getTotalPages(), videos.isFirst(), videos.isLast(), sortBy, direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/topic/{topicId}")
	public ResponseEntity<PagedResponse<Video>> getVideosByTopicId(@PathVariable Long topicId,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Video> videos = videoService.findByTopicId(topicId, page, size, sortBy, direction, keyword);

		PagedResponse<Video> response = new PagedResponse<>(videos.getContent(), page, size, videos.getTotalElements(),
				videos.getTotalPages(), videos.isFirst(), videos.isLast(), sortBy, direction);

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<Video> createVideo(@Valid @RequestBody VideoDTO videoDTO) {
		return ResponseEntity.status(201).body(videoService.createVideo(videoDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Video> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoDTO videoDTO) {
		return ResponseEntity.status(200).body(videoService.updateVideo(id, videoDTO));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		videoService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
		videoService.deleteVideo(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{videoId}/word/{wordId}")
	public ResponseEntity<Video> addWordToVideo(@PathVariable Long videoId, @PathVariable Long wordId) {
		return ResponseEntity.ok(videoService.addWordToVideo(videoId, wordId));
	}

	@DeleteMapping("/{videoId}/word/{wordId}")
	public ResponseEntity<Video> removeWordFromVideo(@PathVariable Long videoId, @PathVariable Long wordId) {
		return ResponseEntity.ok(videoService.removeWordFromVideo(videoId, wordId));
	}

	@DeleteMapping("/{videoId}/word")
	public ResponseEntity<Video> removeAllWordsFromVideo(@PathVariable Long videoId) {
		return ResponseEntity.ok(videoService.removeAllWordsFromVideo(videoId));
	}
}
