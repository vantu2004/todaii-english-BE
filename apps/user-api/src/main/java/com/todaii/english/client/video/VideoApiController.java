package com.todaii.english.client.video;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionaryEntry_;
import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/video")
public class VideoApiController {
	private static final String SORT_DIRECTION = "asc";

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

	@GetMapping("/{id}/entry")
	public ResponseEntity<PagedResponse<DictionaryEntry>> getPagedVocabulary(@PathVariable Long id,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size) {
		Page<DictionaryEntry> entries = videoService.getPagedVocabulary(id, page, size);

		PagedResponse<DictionaryEntry> response = new PagedResponse<>(entries.getContent(), page, size,
				entries.getTotalElements(), entries.getTotalPages(), entries.isFirst(), entries.isLast(),
				DictionaryEntry_.HEADWORD, SORT_DIRECTION);

		return ResponseEntity.ok(response);
	}

	// lấy n video liên quan
	@GetMapping("/{id}/related")
	public ResponseEntity<List<Video>> getRelated(@PathVariable Long id,
			@RequestParam(defaultValue = "5") @Min(value = 1, message = "Size must be at least 1") int limit) {
		return ResponseEntity.ok(videoService.getRelatedVideos(id, limit));
	}

	// lấy videos theo ngày, định dạng YYYY-MM-DD (đúng format, số lượng ký tự)
	@GetMapping("/by-date/{date}")
	public ResponseEntity<PagedResponse<Video>> getArticlesByDate(
			@PathVariable @NotBlank(message = "Date must not be blank") String date,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		LocalDate parsedDate;
		try {
			parsedDate = LocalDate.parse(date);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format. Expected format is YYYY-MM-DD");
		}

		Page<Video> videos = videoService.getVideosByDate(parsedDate, page, size, sortBy, direction);

		return ResponseEntity.ok(new PagedResponse<>(videos.getContent(), page, size, videos.getTotalElements(),
				videos.getTotalPages(), videos.isFirst(), videos.isLast(), sortBy, direction));
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
