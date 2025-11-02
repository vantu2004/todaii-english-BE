package com.todaii.english.server.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
@Rollback(true)
class VideoLyricLineRepositoryTests {

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private VideoLyricLineRepository repository;

	private Video video;

	@BeforeEach
	void setup() {
		video = Video.builder().title("Test Video CRUD").authorName("John Doe").providerName("YouTube")
				.providerUrl("https://youtube.com").thumbnailUrl("https://img.youtube.com/test.jpg")
				.embedHtml("<iframe></iframe>").videoUrl("https://youtube.com/watch?v=abc").cefrLevel(CefrLevel.A1)
				.enabled(true).build();

		video = videoRepository.save(video);
	}

	private VideoLyricLine createLyric(int order, String en, String vi) {
		return VideoLyricLine.builder().lineOrder(order).startMs(order * 1000).endMs(order * 2000).textEn(en).textVi(vi)
				.video(video).build();
	}

	@Test
	@DisplayName("Create - Lưu lyric mới thành công")
	void testCreateLyric() {
		VideoLyricLine saved = repository.save(createLyric(1, "Hello world", "Xin chào"));
		assertNotNull(saved.getId());
		assertThat(saved.getTextEn()).isEqualTo("Hello world");
	}

	@Test
	@DisplayName("Read - findAll(videoId) trả về lyrics theo lineOrder ASC")
	void testFindAllSortedByLineOrder() {
		repository.saveAll(List.of(createLyric(2, "Line 2", "Dòng 2"), createLyric(1, "Line 1", "Dòng 1"),
				createLyric(3, "Line 3", "Dòng 3")));

		List<VideoLyricLine> result = repository.findAll(video.getId());

		assertThat(result).hasSize(3);
		assertThat(result.get(0).getLineOrder()).isEqualTo(1);
		assertThat(result.get(2).getLineOrder()).isEqualTo(3);
	}

	@Test
	@DisplayName("Update - Cập nhật lyric thành công")
	void testUpdateLyric() {
		VideoLyricLine saved = repository.save(createLyric(1, "Old", "Cũ"));
		saved.setTextEn("Updated");
		saved.setTextVi("Mới");

		VideoLyricLine updated = repository.save(saved);
		assertThat(updated.getTextEn()).isEqualTo("Updated");
	}

	@Test
	@DisplayName("Delete - Xóa toàn bộ lyric theo videoId")
	void testDeleteAllByVideoId() {
		repository.saveAll(List.of(createLyric(1, "A", "1"), createLyric(2, "B", "2"), createLyric(3, "C", "3")));

		repository.deleteAllByVideoId(video.getId());
		assertThat(repository.findAll(video.getId())).isEmpty();
	}

	@Test
	@DisplayName("Read - findAll(videoId) trả về danh sách rỗng nếu video chưa có lyric")
	void testFindAllEmpty() {
		Video newVideo = videoRepository.save(Video.builder().title("Empty").authorName("Jane Doe")
				.providerName("YouTube").providerUrl("https://youtube.com")
				.thumbnailUrl("https://img.youtube.com/empty.jpg").embedHtml("<iframe></iframe>")
				.videoUrl("https://youtube.com/watch?v=xyz").cefrLevel(CefrLevel.A2).enabled(true).build());

		assertThat(repository.findAll(newVideo.getId())).isEmpty();
	}
}
