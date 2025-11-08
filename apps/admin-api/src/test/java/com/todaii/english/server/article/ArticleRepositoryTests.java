package com.todaii.english.server.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.core.entity.Topic;
import com.todaii.english.server.topic.TopicRepository; // Giả định TopicRepository tồn tại
import com.todaii.english.shared.enums.CefrLevel;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class ArticleRepositoryTests {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private TopicRepository topicRepository; // Cần thiết để test quan hệ Many-to-Many

	private Article savedArticle1;
	private Topic savedTopic1;

	@BeforeEach
	void setup() {
		// Xóa dữ liệu cũ
		articleRepository.deleteAll();
		topicRepository.deleteAll();

		// 1. Tạo Topic
		Topic topic = Topic.builder().name("Technology").alias("technology").build();
		savedTopic1 = topicRepository.save(topic);

		// 2. Tạo Article
		Article article1 = Article.builder().title("AI Breakthrough in 2024")
				.description("A detailed report on new AI models.").sourceName("Tech Times").author("Alice")
				.cefrLevel(CefrLevel.C1).enabled(true).publishedAt(LocalDateTime.now())
				.topics(new HashSet<>(Set.of(savedTopic1))) // Liên kết Topic
				.build();

		// Thêm Paragraph
		ArticleParagraph para1 = ArticleParagraph.builder().paraOrder(1).textEn("First paragraph.").article(article1)
				.build();
		ArticleParagraph para2 = ArticleParagraph.builder().paraOrder(2).textEn("Second paragraph.").article(article1)
				.build();
		article1.setParagraphs(new HashSet<>(Set.of(para1, para2)));

		savedArticle1 = articleRepository.save(article1);
	}

	// ==================== CRUD ====================

	@Test
	@DisplayName("CREATE - Lưu Article mới thành công")
	void testCreateArticle() {
		Article newArticle = Article.builder().title("New Article Title").cefrLevel(CefrLevel.A2).build();

		Article saved = articleRepository.save(newArticle);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getTitle()).isEqualTo("New Article Title");
		assertThat(saved.getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("READ - Lấy Article theo ID thành công")
	void testFindById_Success() {
		Optional<Article> found = articleRepository.findById(savedArticle1.getId());

		assertThat(found).isPresent();
		assertThat(found.get().getTitle()).isEqualTo("AI Breakthrough in 2024");
	}

	@Test
	@DisplayName("READ - Quan hệ OneToMany (Paragraphs) được tải đúng thứ tự")
	void testFindById_LoadParagraphs() {
		Article found = articleRepository.findById(savedArticle1.getId()).get();

		// Kiểm tra Paragraph được lưu
		assertThat(found.getParagraphs()).hasSize(2);

		// Kiểm tra thứ tự do @OrderBy("paraOrder ASC")
		List<ArticleParagraph> orderedParas = found.getParagraphs().stream()
				.sorted((p1, p2) -> p1.getParaOrder().compareTo(p2.getParaOrder())).toList();

		assertThat(orderedParas.get(0).getParaOrder()).isEqualTo(1);
		assertThat(orderedParas.get(0).getTextEn()).isEqualTo("First paragraph.");
	}

	@Test
	@DisplayName("UPDATE - Cập nhật Title và CefrLevel thành công")
	void testUpdateArticle() {
		savedArticle1.setTitle("Updated Title");
		savedArticle1.setCefrLevel(CefrLevel.B2);

		Article updated = articleRepository.save(savedArticle1);

		assertThat(updated.getTitle()).isEqualTo("Updated Title");
		assertThat(updated.getCefrLevel()).isEqualTo(CefrLevel.B2);
	}

	@Test
	@DisplayName("DELETE - Xóa Article theo ID thành công")
	void testDeleteArticle() {
		Long idToDelete = savedArticle1.getId();

		articleRepository.deleteById(idToDelete);

		Optional<Article> result = articleRepository.findById(idToDelete);
		assertThat(result).isEmpty();
	}

	// ==================== SEARCH / PAGED ====================

	@Test
	@DisplayName("Search: trả về tất cả khi topicId và keyword là NULL")
	void testSearch_Nulls() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Article> result = articleRepository.search(null, null, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("AI Breakthrough in 2024");
	}

	@Test
	@DisplayName("Search: tìm kiếm theo keyword (title) không phân biệt chữ hoa/thường")
	void testSearch_ByKeywordTitle() {
		Pageable pageable = PageRequest.of(0, 10);
		// Tìm kiếm một phần Title "breakthrough"
		Page<Article> result = articleRepository.search(null, "breakthrough", pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("AI Breakthrough in 2024");
	}

	@Test
	@DisplayName("Search: tìm kiếm theo keyword (sourceName)")
	void testSearch_ByKeywordSourceName() {
		Pageable pageable = PageRequest.of(0, 10);
		// Tìm kiếm một phần Source Name "Tech"
		Page<Article> result = articleRepository.search(null, "tech", pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getSourceName()).isEqualTo("Tech Times");
	}

	@Test
	@DisplayName("Search: tìm kiếm theo TopicId thành công")
	void testSearch_ByTopicId() {
		Pageable pageable = PageRequest.of(0, 10);
		// savedArticle1 được liên kết với savedTopic1
		Page<Article> result = articleRepository.search(savedTopic1.getId(), null, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("AI Breakthrough in 2024");
	}

	@Test
	@DisplayName("Search: tìm kiếm theo TopicId không có kết quả")
	void testSearch_ByTopicId_NoResults() {
		Pageable pageable = PageRequest.of(0, 10);
		// Giả định Topic ID 99 không tồn tại hoặc không có bài viết nào liên kết
		Page<Article> result = articleRepository.search(99L, null, pageable);

		assertThat(result.getTotalElements()).isEqualTo(0);
	}
}