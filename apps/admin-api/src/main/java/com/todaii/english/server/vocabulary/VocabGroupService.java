package com.todaii.english.server.vocabulary;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.VocabGroup;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VocabGroupService {
	private final VocabGroupRepository vocabGroupRepository;

	public List<VocabGroup> findAll() {
		return vocabGroupRepository.findAll();
	}

	public Page<VocabGroup> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return vocabGroupRepository.search(keyword, pageable);
	}

	public VocabGroup findById(Long id) {
		return vocabGroupRepository.findById(id)
				.orElseThrow(() -> new BusinessException(404, "Vocabulary group not found"));
	}

	public VocabGroup create(String name) {
		String alias = toAlias(name);
		if (vocabGroupRepository.existsByAlias(alias)) {
			throw new BusinessException(409, "Alias already exists: " + alias);
		}

		VocabGroup vocabGroup = VocabGroup.builder().name(name).alias(alias).build();

		return vocabGroupRepository.save(vocabGroup);
	}

	public VocabGroup update(Long id, String name) {
		VocabGroup vocabGroup = findById(id);

		// Nếu name không thay đổi thì bỏ qua alias check
		if (vocabGroup.getName().equalsIgnoreCase(name.trim())) {
			return vocabGroup; // không cần save vì không thay đổi gì
		}

		// Sinh alias mới từ name
		String alias = toAlias(name);

		// Check trùng alias với topic khác
		boolean aliasExists = vocabGroupRepository.existsByAlias(alias);
		if (aliasExists && !alias.equals(vocabGroup.getAlias())) {
			throw new BusinessException(409, "Alias already exists: " + alias);
		}

		// Update dữ liệu
		vocabGroup.setName(name.trim());
		vocabGroup.setAlias(alias);

		return vocabGroupRepository.save(vocabGroup);
	}

	private String toAlias(String name) {
		return name.trim().toLowerCase().replaceAll("\\s+", "-");
	}

	public void softDelete(Long id) {
		VocabGroup vocabGroup = findById(id);
		vocabGroup.setName("deleted-" + vocabGroup.getName());
		vocabGroup.setAlias("deleted-" + vocabGroup.getAlias());
		vocabGroup.setIsDeleted(true);

		vocabGroupRepository.save(vocabGroup);
	}

	public void toggleEnabled(Long id) {
		VocabGroup vocabGroup = findById(id);
		vocabGroup.setEnabled(!vocabGroup.getEnabled());

		vocabGroupRepository.save(vocabGroup);
	}
}
