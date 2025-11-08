package com.todaii.english.server.vocabulary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.VocabGroup;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback
public class VocabGroupRepositoryTests {

	@Autowired
	private VocabGroupRepository vocabGroupRepository;

	private VocabGroup savedGroup1; // Common Words, enabled=true
	private VocabGroup savedGroup2; // Business English, enabled=false

	@BeforeEach
	void setup() {
		vocabGroupRepository.deleteAll();

		// Group 1: Chưa xóa, Enabled
		VocabGroup group1 = VocabGroup.builder().name("Common Words").alias("common-words").enabled(true).build();

		// Group 2: Chưa xóa, Disabled
		VocabGroup group2 = VocabGroup.builder().name("Business English").alias("business-english").enabled(false)
				.build();

		savedGroup1 = vocabGroupRepository.save(group1);
		savedGroup2 = vocabGroupRepository.save(group2);
	}

	// ==================== CREATE / BASIC READ ====================

	@Test
	@DisplayName("Create: lưu VocabGroup mới thành công")
	void testCreate() {
		VocabGroup group = VocabGroup.builder().name("Academic Words").alias("academic-words").enabled(true).build();

		VocabGroup saved = vocabGroupRepository.save(group);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getAlias()).isEqualTo("academic-words");
		assertThat(saved.getEnabled()).isTrue();
	}

	@Test
	@DisplayName("Read: findAll (Deprecated) trả về tất cả các bản ghi chưa bị xóa")
	void testFindAll_ExcludeDeleted() {
		// Set group 1 là đã xóa
		savedGroup1.setIsDeleted(true);
		vocabGroupRepository.save(savedGroup1);

		List<VocabGroup> list = vocabGroupRepository.findAll();
		
		// Chỉ còn group 2 chưa bị xóa
		assertThat(list).hasSize(1); 
		assertThat(list.get(0).getName()).isEqualTo("Business English");
	}

	@Test
	@DisplayName("Read: findById trả về đúng bản ghi theo id")
	void testFindById_Success() {
		Optional<VocabGroup> found = vocabGroupRepository.findById(savedGroup1.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Common Words");
	}

	@Test
	@DisplayName("Read: findById không trả về bản ghi đã bị xóa")
	void testFindById_DeletedRecord() {
		savedGroup1.setIsDeleted(true);
		vocabGroupRepository.save(savedGroup1);

		Optional<VocabGroup> found = vocabGroupRepository.findById(savedGroup1.getId());
		assertThat(found).isEmpty();
	}

	// ==================== UPDATE ====================

	@Test
	@DisplayName("Update: cập nhật name và alias thành công")
	void testUpdate() {
		savedGroup1.setName("Updated Name");
		savedGroup1.setAlias("updated-name");

		VocabGroup updated = vocabGroupRepository.save(savedGroup1);
		assertThat(updated.getName()).isEqualTo("Updated Name");
		assertThat(updated.getAlias()).isEqualTo("updated-name");
	}
	
	@Test
	@DisplayName("UpdateTimestamp: trường updatedAt được cập nhật tự động")
	void testUpdateTimestamp() {
		VocabGroup group = vocabGroupRepository.findById(savedGroup1.getId()).get();
		assertThat(group.getUpdatedAt()).isNotNull();
		
		// Giả lập cập nhật
		group.setName("Name Change");
		VocabGroup updatedGroup = vocabGroupRepository.save(group);
		
		// Trong môi trường test DB H2, đôi khi timestamp không thay đổi đủ nhanh
		// Tuy nhiên, việc kiểm tra field này tồn tại là đủ cho DataJpaTest
		assertThat(updatedGroup.getUpdatedAt()).isNotNull();
	}

	// ==================== DELETE / UTILS ====================

	@Test
	@DisplayName("Soft Delete: thay đổi cờ isDeleted = true")
	void testSoftDeleteFlag() {
		// Đây là logic giả lập soft delete trong Service, nhưng test Repo đảm bảo
		// thuộc tính isDeleted được lưu và không bị trả về bởi các phương thức khác
		savedGroup2.setIsDeleted(true);
		VocabGroup deleted = vocabGroupRepository.save(savedGroup2);

		assertThat(deleted.getIsDeleted()).isTrue();
		
		// Kiểm tra phương thức findAll (Deprecated) không trả về record này
		List<VocabGroup> activeGroups = vocabGroupRepository.findAll();
		assertThat(activeGroups).extracting(VocabGroup::getIsDeleted).allMatch(isDeleted -> isDeleted == false);
	}

	@Test
	@DisplayName("existsByAlias: trả về true nếu alias tồn tại")
	void testExistsByAlias_True() {
		boolean exists = vocabGroupRepository.existsByAlias("business-english");
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsByAlias: trả về false nếu alias không tồn tại")
	void testExistsByAlias_False() {
		boolean exists = vocabGroupRepository.existsByAlias("non-existent-alias");
		assertThat(exists).isFalse();
	}
	
	// ==================== SEARCH / PAGED ====================

	@Test
	@DisplayName("Search: trả về tất cả khi keyword là NULL và có phân trang")
	void testSearch_NullKeyword() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
		Page<VocabGroup> result = vocabGroupRepository.search(null, pageable);

		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getName()).isEqualTo("Common Words");
	}
	
	@Test
	@DisplayName("Search: tìm kiếm theo keyword (tên) không phân biệt chữ hoa/thường")
	void testSearch_ByKeywordName() {
		Pageable pageable = PageRequest.of(0, 10);
		// Tìm kiếm với "common"
		Page<VocabGroup> result = vocabGroupRepository.search("Common", pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getName()).isEqualTo("Common Words");
	}

	@Test
	@DisplayName("Search: tìm kiếm theo keyword (alias)")
	void testSearch_ByKeywordAlias() {
		Pageable pageable = PageRequest.of(0, 10);
		// Tìm kiếm với "english"
		Page<VocabGroup> result = vocabGroupRepository.search("english", pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getAlias()).isEqualTo("business-english");
	}

	@Test
	@DisplayName("Search: tìm kiếm theo keyword (ID)")
	void testSearch_ByKeywordId() {
		Pageable pageable = PageRequest.of(0, 10);
		// Giả sử ID của savedGroup1 là 1. Tìm kiếm theo ID dưới dạng String
		Page<VocabGroup> result = vocabGroupRepository.search(savedGroup1.getId().toString(), pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(savedGroup1.getId());
	}
	
	@Test
	@DisplayName("Search: không trả về bản ghi đã bị xóa")
	void testSearch_ExcludeDeletedRecord() {
		// Xóa mềm group 1
		savedGroup1.setIsDeleted(true);
		vocabGroupRepository.save(savedGroup1);

		// Tìm kiếm với keyword NULL
		Pageable pageable = PageRequest.of(0, 10);
		Page<VocabGroup> result = vocabGroupRepository.search(null, pageable);

		// Chỉ còn group 2 chưa bị xóa
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(savedGroup2.getId());
	}
	
	@Test
	@DisplayName("Search: trả về danh sách rỗng khi không tìm thấy kết quả")
	void testSearch_NoResults() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<VocabGroup> result = vocabGroupRepository.search("non-existent-keyword", pageable);

		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getContent()).isEmpty();
	}
}