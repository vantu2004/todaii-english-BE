package com.todaii.english.server.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.Video;
import com.todaii.english.core.entity.VideoLyricLine;
import com.todaii.english.shared.enums.CefrLevel;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback
class VideoLyricLineRepositoryTests {

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private VideoLyricLineRepository videoLyricLineRepository;

	private Video video;

	@BeforeEach
	void setup() {
		video = Video.builder().title("Test Video CRUD").authorName("John Doe").providerName("YouTube")
				.providerUrl("https://youtube.com").thumbnailUrl("https://img.youtube.com/test.jpg")
				.embedHtml("<iframe></iframe>").videoUrl("https://youtube.com/watch?v=abc").cefrLevel(CefrLevel.A1)
				.enabled(true).build();

		video = videoRepository.save(video);
	}

	// Helper
	private VideoLyricLine createLyric(int order, String en, String vi) {
		return VideoLyricLine.builder().lineOrder(order).startMs(order * 1000).endMs(order * 2000).textEn(en).textVi(vi)
				.video(video).build();
	}

	@Test
	@DisplayName("Create - Lưu lyric mới thành công")
	void testCreateLyric() {
		VideoLyricLine lyric = VideoLyricLine.builder().lineOrder(1).startMs(0).endMs(2000).textEn("Hello world")
				.textVi("Xin chào thế giới").video(video).build();

		VideoLyricLine saved = videoLyricLineRepository.save(lyric);

		assertNotNull(saved.getId());
		assertThat(saved.getTextEn()).isEqualTo("Hello world");
		assertThat(saved.getVideo().getId()).isEqualTo(video.getId());
	}

	@Test
	@DisplayName("Read - Tìm lyric theo ID và video ID")
	void testFindByIdAndVideoId() {
		VideoLyricLine lyric = createLyric(1, "One", "Một");
		VideoLyricLine saved = videoLyricLineRepository.save(lyric);

		Optional<VideoLyricLine> found = videoLyricLineRepository.findByIdAndVideoId(saved.getId(), video.getId());

		assertTrue(found.isPresent());
		assertEquals("One", found.get().getTextEn());
	}

	@Test
	@DisplayName("Read - Tìm tất cả lyric theo videoId, có sắp xếp ORDER ASC")
	void testFindAllSortedByLineOrder() {
		videoLyricLineRepository.saveAll(List.of(createLyric(2, "Line 2", "Dòng 2"), createLyric(1, "Line 1", "Dòng 1"),
				createLyric(3, "Line 3", "Dòng 3")));

		List<VideoLyricLine> result = videoLyricLineRepository.findAll(video.getId());

		assertThat(result).hasSize(3);
		assertThat(result.get(0).getLineOrder()).isEqualTo(1);
		assertThat(result.get(2).getLineOrder()).isEqualTo(3);
	}

	@Test
	@DisplayName("Update - Cập nhật lyric thành công")
	void testUpdateLyric() {
		VideoLyricLine lyric = createLyric(1, "Old text", "Cũ");
		VideoLyricLine saved = videoLyricLineRepository.save(lyric);

		saved.setTextEn("Updated text");
		saved.setTextVi("Mới");

		VideoLyricLine updated = videoLyricLineRepository.save(saved);

		assertThat(updated.getTextEn()).isEqualTo("Updated text");
		assertThat(updated.getTextVi()).isEqualTo("Mới");
	}

	@Test
	@DisplayName("existsByIdAndVideoId() - kiểm tra tồn tại lyric theo video")
	void testExistsByIdAndVideoId() {
		VideoLyricLine lyric = videoLyricLineRepository.save(createLyric(1, "Check exists", "Tồn tại"));

		boolean exists = videoLyricLineRepository.existsByIdAndVideoId(lyric.getId(), video.getId());
		boolean notExists = videoLyricLineRepository.existsByIdAndVideoId(lyric.getId(), 9999L);

		assertTrue(exists);
		assertFalse(notExists);
	}

	@Test
	@DisplayName("Delete - Xóa lyric cụ thể theo lineId và videoId")
	void testDeleteByIdAndVideoId() {
		VideoLyricLine lyric = videoLyricLineRepository.save(createLyric(1, "To delete", "Xóa"));

		assertTrue(videoLyricLineRepository.existsByIdAndVideoId(lyric.getId(), video.getId()));

		videoLyricLineRepository.deleteByIdAndVideoId(lyric.getId(), video.getId());

		assertFalse(videoLyricLineRepository.existsByIdAndVideoId(lyric.getId(), video.getId()));
	}

	@Test
	@DisplayName("Delete - Xóa toàn bộ lyric theo videoId")
	void testDeleteAllByVideoId() {
		videoLyricLineRepository
				.saveAll(List.of(createLyric(1, "A", "1"), createLyric(2, "B", "2"), createLyric(3, "C", "3")));

		List<VideoLyricLine> before = videoLyricLineRepository.findAll(video.getId());
		assertThat(before).hasSize(3);

		videoLyricLineRepository.deleteAllByVideoId(video.getId());

		List<VideoLyricLine> after = videoLyricLineRepository.findAll(video.getId());
		assertThat(after).isEmpty();
	}

	@Test
	@DisplayName("Delete - Xóa lyric không tồn tại không gây lỗi")
	void testDeleteNonExistent() {
		assertDoesNotThrow(() -> videoLyricLineRepository.deleteByIdAndVideoId(9999L, video.getId()));
	}

	@Test
	@DisplayName("Read - findAll(videoId) trả về danh sách rỗng nếu video chưa có lyric")
	void testFindAllEmpty() {
		Video newVideo = videoRepository.save(Video.builder().title("Empty Video").authorName("Jane Doe")
				.providerName("YouTube").providerUrl("https://youtube.com")
				.thumbnailUrl("https://img.youtube.com/test2.jpg").embedHtml("<iframe></iframe>")
				.videoUrl("https://youtube.com/watch?v=xyz").cefrLevel(CefrLevel.A2).enabled(true).build());

		List<VideoLyricLine> result = videoLyricLineRepository.findAll(newVideo.getId());
		assertThat(result).isEmpty();
	}
}
