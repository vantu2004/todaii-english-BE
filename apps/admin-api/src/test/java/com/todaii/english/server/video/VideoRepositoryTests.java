package com.todaii.english.server.video;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.enums.CefrLevel;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class VideoRepositoryTests {

	@Autowired
	private VideoRepository videoRepository;

	private Video buildSampleVideo(String url) {
		return Video.builder().title("Test Video").authorName("Tester").providerName("YouTube")
				.providerUrl("https://www.youtube.com/").thumbnailUrl("https://i.ytimg.com/sample.jpg")
				.embedHtml("<iframe src='" + url + "'></iframe>").videoUrl(url).views(10).cefrLevel(CefrLevel.A2)
				.enabled(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
	}

	@Test
	@DisplayName("Tạo video mới thành công")
	void testCreateVideo() {
		Video video = buildSampleVideo("https://youtube.com/watch?v=abc");
		Video saved = videoRepository.save(video);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getTitle()).isEqualTo("Test Video");
		assertThat(saved.getVideoUrl()).contains("youtube.com");
	}

	@Test
	@DisplayName("Tìm video theo ID tồn tại")
	void testFindByIdFound() {
		Video video = buildSampleVideo("https://youtube.com/watch?v=123");
		Video saved = videoRepository.save(video);

		Optional<Video> found = videoRepository.findById(saved.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getTitle()).isEqualTo("Test Video");
	}

	@Test
	@DisplayName("Tìm video theo ID không tồn tại")
	void testFindByIdNotFound() {
		Optional<Video> found = videoRepository.findById(999999L);
		assertThat(found).isNotPresent();
	}

	@Test
	@DisplayName("Lấy danh sách tất cả video")
	void testFindAll() {
		videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=x1"));
		videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=x2"));

		List<Video> list = videoRepository.findAll();
		assertThat(list).isNotEmpty();
		assertThat(list.size()).isGreaterThanOrEqualTo(2);
	}

	@Test
	@DisplayName("Kiểm tra tồn tại video theo URL - có tồn tại")
	void testExistsByVideoUrlTrue() {
		String url = "https://youtube.com/watch?v=exist";
		videoRepository.save(buildSampleVideo(url));

		boolean exists = videoRepository.existsByVideoUrl(url);
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Kiểm tra tồn tại video theo URL - không tồn tại")
	void testExistsByVideoUrlFalse() {
		boolean exists = videoRepository.existsByVideoUrl("https://youtube.com/watch?v=none");
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("Cập nhật thông tin video thành công")
	void testUpdateVideo() {
		Video video = videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=up1"));
		video.setTitle("Updated Title");

		Video updated = videoRepository.save(video);

		assertThat(updated.getTitle()).isEqualTo("Updated Title");
		assertThat(updated.getId()).isEqualTo(video.getId());
	}

	@Test
	@DisplayName("Xóa video thành công")
	void testDeleteVideo() {
		Video video = videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=del"));
		Long id = video.getId();

		videoRepository.deleteById(id);
		Optional<Video> found = videoRepository.findById(id);

		assertThat(found).isNotPresent();
	}

	@Test
	@DisplayName("Tìm kiếm video theo keyword - tìm thấy kết quả")
	void testSearchByKeywordFound() {
		// given
		Video v1 = buildSampleVideo("https://youtube.com/watch?v=abc");
		v1.setTitle("Learn English with Music");
		Video v2 = buildSampleVideo("https://youtube.com/watch?v=xyz");
		v2.setTitle("Cooking Tutorial");

		videoRepository.saveAll(List.of(v1, v2));

		// when
		Pageable pageable = PageRequest.of(0, 10);
		Page<Video> page = videoRepository.search(null, "music", pageable);

		// then
		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getTitle()).containsIgnoringCase("music");
	}

	@Test
	@DisplayName("Tìm kiếm video không có keyword - trả về tất cả")
	void testSearchWithoutKeyword() {
		videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=1"));
		videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=2"));

		Pageable pageable = PageRequest.of(0, 10);
		Page<Video> page = videoRepository.search(null, null, pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);
	}

	@Test
	@DisplayName("Tìm kiếm video theo topicId - không có kết quả")
	void testSearchByTopicIdNotFound() {
		videoRepository.save(buildSampleVideo("https://youtube.com/watch?v=1234"));

		Pageable pageable = PageRequest.of(0, 10);
		Page<Video> page = videoRepository.search(999L, null, pageable);

		assertThat(page.getContent()).isEmpty();
	}

}
