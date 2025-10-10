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
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.VocabGroup;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback
public class VocabGroupRepositoryTests {

	@Autowired
	private VocabGroupRepository vocabGroupRepository;

	private VocabGroup savedGroup1;
	private VocabGroup savedGroup2;

	@BeforeEach
	void setup() {
		vocabGroupRepository.deleteAll();

		VocabGroup group1 = VocabGroup.builder().name("Common Words").alias("common-words").enabled(true).build();

		VocabGroup group2 = VocabGroup.builder().name("Business English").alias("business-english").enabled(false)
				.build();

		savedGroup1 = vocabGroupRepository.save(group1);
		savedGroup2 = vocabGroupRepository.save(group2);
	}

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
	@DisplayName("Read: findAll trả về tất cả các bản ghi chưa bị xóa")
	void testFindAll() {
		List<VocabGroup> list = vocabGroupRepository.findAll();
		assertThat(list).hasSize(2);
		assertThat(list.get(0).getIsDeleted()).isFalse();
	}

	@Test
	@DisplayName("Read: findById trả về đúng bản ghi theo id")
	void testFindById() {
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
	@DisplayName("Delete: soft delete thay đổi cờ isDeleted = true")
	void testSoftDeleteFlag() {
		savedGroup2.setIsDeleted(true);
		VocabGroup deleted = vocabGroupRepository.save(savedGroup2);

		assertThat(deleted.getIsDeleted()).isTrue();

		// kiểm tra findAll không còn trả về record này
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

	@Test
	@DisplayName("findAll: không trả về bản ghi đã bị xóa")
	void testFindAll_ExcludeDeleted() {
		savedGroup1.setIsDeleted(true);
		vocabGroupRepository.save(savedGroup1);

		List<VocabGroup> list = vocabGroupRepository.findAll();
		assertThat(list).extracting(VocabGroup::getIsDeleted).allMatch(isDeleted -> isDeleted == false);
	}

	@Test
	@DisplayName("UpdateTimestamp: trường updatedAt được cập nhật tự động")
	void testUpdateTimestamp() {
		VocabGroup group = vocabGroupRepository.findById(savedGroup1.getId()).get();
		assertThat(group.getUpdatedAt()).isNotNull();
	}
}
