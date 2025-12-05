package com.todaii.english.server.video;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Video;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.dto.VideoDTO;
import com.todaii.english.shared.response.PagedResponse;
import com.todaii.english.shared.response.YoutubeSearchResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/video")
@Tag(name = "Video", description = "API for managing videos, importing from YouTube, and CRUD operations")
public class VideoApiController {

	private final VideoService videoService;

	@GetMapping("/youtube")
	@Operation(summary = "Import video from YouTube URL", description = "Fetch video metadata from a given YouTube URL.")
	@ApiResponse(responseCode = "200", description = "Video imported successfully", content = @Content(schema = @Schema(implementation = VideoDTO.class)))
	public ResponseEntity<VideoDTO> importFromYoutube(
			@RequestParam @NotBlank @Length(max = 1024) String url) throws BadRequestException {
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
	@Operation(summary = "Get video by ID", description = "Retrieve a single video by its ID.")
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
	@Operation(summary = "Get paginated list of videos", description = "Return a paginated list of videos with optional keyword filtering.")
	public ResponseEntity<PagedResponse<Video>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Video> videos = videoService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<Video> response = new PagedResponse<>(
				videos.getContent(), page, size, videos.getTotalElements(),
				videos.getTotalPages(), videos.isFirst(), videos.isLast(),
				sortBy, direction
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/topic/{topicId}")
	@Operation(summary = "Get videos by Topic ID", description = "Return videos belonging to a specific topic with pagination.")
	public ResponseEntity<PagedResponse<Video>> getVideosByTopicId(
			@PathVariable Long topicId,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Video> videos = videoService.findByTopicId(topicId, page, size, sortBy, direction, keyword);

		PagedResponse<Video> response = new PagedResponse<>(
				videos.getContent(), page, size, videos.getTotalElements(),
				videos.getTotalPages(), videos.isFirst(), videos.isLast(),
				sortBy, direction
		);

		return ResponseEntity.ok(response);
	}

	@PostMapping
	@Operation(summary = "Create new video", description = "Add a new video to the system.")
	public ResponseEntity<Video> createVideo(@Valid @RequestBody VideoDTO videoDTO) {
		return ResponseEntity.status(201).body(videoService.createVideo(videoDTO));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update video", description = "Update an existing video by ID.")
	public ResponseEntity<Video> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoDTO videoDTO) {
		return ResponseEntity.status(200).body(videoService.updateVideo(id, videoDTO));
	}

	@PatchMapping("/{id}/enabled")
	@Operation(summary = "Toggle video enabled status", description = "Enable or disable a video.")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		videoService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete video", description = "Delete a video by its ID.")
	public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
		videoService.deleteVideo(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{videoId}/word/{wordId}")
	@Operation(summary = "Add a word to video", description = "Associate a word with a video.")
	public ResponseEntity<Video> addWordToVideo(@PathVariable Long videoId, @PathVariable Long wordId) {
		return ResponseEntity.ok(videoService.addWordToVideo(videoId, wordId));
	}

	@DeleteMapping("/{videoId}/word/{wordId}")
	@Operation(summary = "Remove a word from video", description = "Remove a single word association from a video.")
	public ResponseEntity<Video> removeWordFromVideo(@PathVariable Long videoId, @PathVariable Long wordId) {
		return ResponseEntity.ok(videoService.removeWordFromVideo(videoId, wordId));
	}

	@DeleteMapping("/{videoId}/word")
	@Operation(summary = "Remove all words from video", description = "Remove all word associations from a video.")
	public ResponseEntity<Video> removeAllWordsFromVideo(@PathVariable Long videoId) {
		return ResponseEntity.ok(videoService.removeAllWordsFromVideo(videoId));
	}
}
