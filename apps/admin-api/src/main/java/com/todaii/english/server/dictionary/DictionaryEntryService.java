package com.todaii.english.server.dictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictionaryEntryService {
	private final DictionaryPort dictionaryPort;
	private final GeminiPort geminiPort;
	private final DictionaryEntryRepository dictionaryEntryRepository;
	private final ObjectMapper objectMapper;
	private final ModelMapper modelMapper;

	public DictionaryApiResponse[] lookupWord(String word) {
		return dictionaryPort.lookupWord(word);
	}

	public List<DictionaryEntry> createWordByGemini(String word) throws Exception {
		// 1️ Tìm trong DB trước
//		List<DictionaryEntry> existing = dictionaryEntryRepository.findByHeadwordContainingIgnoreCase(word.trim());
//		if (!existing.isEmpty()) {
//			return existing;
//		}

		boolean existed = dictionaryEntryRepository.existsByHeadword(word);
		if (existed) {
			throw new BusinessException(409, "The word '" + word + "' already exists in the system.");
		}

		// 2️ Gọi Free Dictionary API
		DictionaryApiResponse[] rawData = lookupWord(word);
		String rawJson = objectMapper.writeValueAsString(rawData);

		// 3️ Gọi Gemini (luôn trả về mảng JSON)
		String prompt = String.format(Gemini.DICTIONARY_PROMPT, rawJson, word);
		String json = cleanJson(geminiPort.generateText(prompt));

		// 4️ Parse mảng DTO
		DictionaryEntryDTO[] dtoArray = objectMapper.readValue(json, DictionaryEntryDTO[].class);

		List<DictionaryEntry> entries = Arrays.stream(dtoArray).map(this::toEntity).map(dictionaryEntryRepository::save)
				.toList();

		return entries;
	}

	public String cleanJson(String raw) {
		return raw.replaceAll("```json", "").replaceAll("```", "").trim();
	}

	public DictionaryEntry toEntity(DictionaryEntryDTO dto) {
		DictionaryEntry entry = DictionaryEntry.builder().headword(dto.getHeadword()).ipa(dto.getIpa())
				.audioUrl(dto.getAudioUrl()).build();

		Set<DictionarySense> senses = buildDictionarySense(dto, entry);

		entry.setSenses(senses);

		dictionaryEntryRepository.save(entry);

		return entry;
	}

	private Set<DictionarySense> buildDictionarySense(DictionaryEntryDTO dto, DictionaryEntry entry) {
		Set<DictionarySense> senses = new HashSet<DictionarySense>(dto.getSenses().stream().map(s -> {
			DictionarySense sense = modelMapper.map(s, DictionarySense.class);
			sense.setEntry(entry);

			return sense;
		}).toList());
		return senses;
	}

	public List<DictionaryEntry> findAll() {
		return dictionaryEntryRepository.findAll();
	}

	public DictionaryEntry findById(Long id) {
		return dictionaryEntryRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Word not found"));
	}

	public DictionaryEntry createWord(DictionaryEntryDTO dto) {
		boolean existed = dictionaryEntryRepository.existsByHeadword(dto.getHeadword());
		if (existed) {
			throw new BusinessException(409, "The word '" + dto.getHeadword() + "' already exists in the system.");
		}

		DictionaryEntry dictionaryEntry = toEntity(dto);

		return dictionaryEntryRepository.save(dictionaryEntry);
	}

	public DictionaryEntry updateWord(Long id, @Valid DictionaryEntryDTO dto) {
		// 1️ Kiểm tra tồn tại
		DictionaryEntry entry = findById(id);

		// 2️ Cập nhật thông tin chính
		entry.setHeadword(dto.getHeadword());
		entry.setIpa(dto.getIpa());
		entry.setAudioUrl(dto.getAudioUrl());

		// 3️ Xử lý senses
		// Xóa toàn bộ sense cũ (orphanRemoval = true đảm bảo tự động xóa trong DB)
		entry.getSenses().clear();

		Set<DictionarySense> senses = buildDictionarySense(dto, entry);

		entry.getSenses().addAll(senses);

		// 4️ Lưu vào DB
		return dictionaryEntryRepository.save(entry);
	}

	public void deleteWord(Long id) {
		if (!dictionaryEntryRepository.existsById(id)) {
			throw new BusinessException(404, "Word not found with id: " + id);
		}

		dictionaryEntryRepository.deleteById(id);
	}

}
