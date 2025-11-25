package com.todaii.english.client.dictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictionaryService {
	private final DictionaryRepository dictionaryRepository;
	private final DictionaryPort dictionaryPort;
	private final GeminiPort geminiPort;
	private final ObjectMapper objectMapper;
	private final ModelMapper modelMapper;

	public DictionaryApiResponse[] lookupWord(String word) {
		return dictionaryPort.lookupWord(word);
	}

	public DictionaryEntry findById(Long id) {
		return dictionaryRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Word not found"));
	}

	public List<DictionaryEntry> findByHeadword(String word) {
		return dictionaryRepository.findAllByHeadword(word);
	}

	public List<DictionaryEntry> getWordByGemini(String word) throws Exception {
		List<DictionaryEntry> dictionaryEntries = dictionaryRepository.findAllByHeadword(word);
		if (!dictionaryEntries.isEmpty()) {
			return dictionaryEntries;
		}
		// Gọi Free Dictionary API
		DictionaryApiResponse[] rawData = lookupWord(word);
		String rawJson = objectMapper.writeValueAsString(rawData);

		// Gọi Gemini (luôn trả về mảng JSON)
		String prompt = String.format(Gemini.DICTIONARY_PROMPT, rawJson, word);
		String rawResponseText = geminiPort.generateText(prompt);

		// Parse mảng DTO
		DictionaryEntryDTO[] dtoArray = objectMapper.readValue(rawResponseText, DictionaryEntryDTO[].class);

		dictionaryEntries = Arrays.stream(dtoArray).map(this::toEntity).map(dictionaryRepository::save).toList();

		return dictionaryEntries;
	}

	public DictionaryEntry toEntity(DictionaryEntryDTO dto) {
		DictionaryEntry entry = DictionaryEntry.builder().headword(dto.getHeadword()).ipa(dto.getIpa())
				.audioUrl(dto.getAudioUrl()).build();

		Set<DictionarySense> senses = buildDictionarySense(dto, entry);

		entry.setSenses(senses);

		dictionaryRepository.save(entry);

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

	public List<String> getRelatedWord(String word) {
		String prompt = String.format(Gemini.RELATED_WORD_PROMPT, word);
		String aiResponse = geminiPort.generateText(prompt);

		try {
			return objectMapper.readValue(aiResponse, new TypeReference<List<String>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Invalid JSON from Gemini: " + aiResponse, e);
		}
	}

	public String askGemini(String prompt) {
		return geminiPort.generateText(prompt);
	}

}
