package com.todaii.english.client.vocabulary;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.VocabGroup;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VocabGroupService {
	private final VocabGroupRepository vocabGroupRepository;

	public List<VocabGroup> findAll() {
		return vocabGroupRepository.findAll();
	}
}
