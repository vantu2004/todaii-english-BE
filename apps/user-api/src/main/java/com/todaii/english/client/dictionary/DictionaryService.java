package com.todaii.english.client.dictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.client.event.EventService;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.enums.EventType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.AIResponse;
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
	private final EventService eventService;

	public DictionaryApiResponse[] lookupWord(Long currentUserId, String word) {
		DictionaryApiResponse[] dictionaryApiResponses = dictionaryPort.lookupWord(word);

		eventService.logUser(currentUserId, EventType.DICTIONARY_API, 1, null);

		return dictionaryApiResponses;
	}

	public DictionaryEntry findById(Long id) {
		return dictionaryRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Word not found"));
	}

	public List<DictionaryEntry> findByHeadword(String word) {
		return dictionaryRepository.findAllByHeadword(word);
	}

	public List<DictionaryEntry> getWordByGemini(Long currentUserId, String word) throws Exception {
		List<DictionaryEntry> dictionaryEntries = dictionaryRepository.findAllByHeadword(word);
		if (!dictionaryEntries.isEmpty()) {
			return dictionaryEntries;
		}
		// Gọi Free Dictionary API
		DictionaryApiResponse[] rawData = lookupWord(currentUserId, word);
		String rawJson = objectMapper.writeValueAsString(rawData);

		// Gọi Gemini (luôn trả về mảng JSON)
		String prompt = String.format(Gemini.DICTIONARY_PROMPT, rawJson, word);
		String rawResponseText = askGemini(currentUserId, prompt);

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

	public List<String> getRelatedWord(Long currentUserId, String word) {
		String prompt = String.format(Gemini.RELATED_WORD_PROMPT, word);
		String responseText = askGemini(currentUserId, prompt);

		try {
			return objectMapper.readValue(responseText, new TypeReference<List<String>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Invalid JSON from Gemini: " + responseText, e);
		}
	}

	public String askGemini(Long currentUserId, String prompt) {
		AIResponse aiResponse = geminiPort.generateText(prompt);
		eventService.logUser(currentUserId, EventType.AI_REQUEST, 1,
				Map.of("input_token", aiResponse.getInputToken(), "output_token", aiResponse.getOutputToken()));

		return aiResponse.getText();
	}

}
