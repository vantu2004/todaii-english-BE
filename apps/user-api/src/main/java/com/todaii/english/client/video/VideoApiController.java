package com.todaii.english.client.video;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/video")
public class VideoApiController {
	private final VideoService videoService;

	// lấy n video gần đây nhất
	@GetMapping("/latest")
	public ResponseEntity<List<Video>> getLatest(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		return ResponseEntity.ok(videoService.getLatestVideos(size));
	}

	// lấy top n video
	@GetMapping("/top")
	public ResponseEntity<List<Video>> getTop(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		return ResponseEntity.ok(videoService.getTopVideos(size));
	}

	// lấy chi tiết video
	@GetMapping("/{id}")
	public ResponseEntity<Video> findById(@PathVariable Long id) {
		return ResponseEntity.ok(videoService.findById(id));
	}

	// lấy n video liên quan
	@GetMapping("/{id}/related")
	public ResponseEntity<List<Video>> getRelated(@PathVariable Long id,
			@RequestParam(defaultValue = "5") @Min(value = 1, message = "Size must be at least 1") int limit) {
		return ResponseEntity.ok(videoService.getRelatedVideos(id, limit));
	}

	// lọc video
	@GetMapping("/filter")
	public ResponseEntity<PagedResponse<Video>> filter(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) CefrLevel cefrLevel, @RequestParam(required = false) Integer minViews,
			@RequestParam(required = false) String alias,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		Page<Video> filterVideos = videoService.filterVideos(keyword, cefrLevel, minViews, alias, page, size, sortBy,
				direction);

		PagedResponse<Video> pagedResponse = new PagedResponse<>(filterVideos.getContent(), page, size,
				filterVideos.getTotalElements(), filterVideos.getTotalPages(), filterVideos.isFirst(),
				filterVideos.isLast(), sortBy, direction);

		return ResponseEntity.ok(pagedResponse);
	}

	// lấy danh sách videos đc lưu bởi user
	@GetMapping("/saved")
	public ResponseEntity<List<Video>> getSavedVideosByUserId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(videoService.getSavedVideosByUserId(currentUserId));
	}

	// check video có đc lưu bởi user ko
	@GetMapping("/{videoId}/is-saved")
	public ResponseEntity<Boolean> isSavedByUser(Authentication authentication, @PathVariable Long videoId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(videoService.isSavedByUser(videoId, currentUserId));
	}

}
