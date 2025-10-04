package com.todaii.english.server.dictionary;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.enums.PartOfSpeech;
import com.todaii.english.shared.response.DictionaryApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictionaryService {
	private final DictionaryPort dictionaryPort;
	private final GeminiPort geminiPort;
	private final ObjectMapper objectMapper;
	private final DictionaryEntryReposiroty dictionaryEntryReposiroty;
	private final DictionarySenseRepository dictionarySenseRepository;

	public DictionaryApiResponse[] lookupWord(String word) {
		return dictionaryPort.lookupWord(word);
	}

	public List<DictionaryEntry> search(String word) throws Exception {
		// 1. Tìm trong DB trước
		List<DictionaryEntry> existing = dictionaryEntryReposiroty.findByHeadwordContainingIgnoreCase(word.trim());

		if (!existing.isEmpty()) {
			return existing; // trả tất cả từ gần giống
		}

		// 2. Nếu DB không có, gọi AI tạo entry mới
		DictionaryApiResponse[] rawData = lookupWord(word);
		String rawJson = objectMapper.writeValueAsString(rawData);

		String prompt = String.format(Gemini.DICTIONARY_PROMPT, rawJson, word);
		String json = cleanJson(geminiPort.generateText(prompt));

		DictionaryEntryDTO dto = parseJson(json);
		DictionaryEntry newEntry = toEntity(dto);

		return List.of(newEntry);
	}

	public String cleanJson(String raw) {
		return raw.replaceAll("```json", "").replaceAll("```", "").trim();
	}

	public DictionaryEntryDTO parseJson(String json) throws Exception {
		return objectMapper.readValue(json, DictionaryEntryDTO.class);
	}

	public DictionaryEntry toEntity(DictionaryEntryDTO dto) {
		DictionaryEntry entry = DictionaryEntry.builder().headword(dto.getHeadword()).ipa(dto.getIpa())
				.audioUrl(dto.getAudioUrl()).build();

		List<DictionarySense> senses = dto.getSenses().stream().map(s -> {
			DictionarySense sense = DictionarySense.builder().pos(PartOfSpeech.valueOf(s.getPos()))
					.meaning(s.getMeaning()).definition(s.getDefinition()).example(s.getExample())
					.synonyms(s.getSynonyms()).collocations(s.getCollocations()).entry(entry) // gán back-reference
					.build();
			return sense;
		}).toList();

		entry.setSenses(senses);

		dictionaryEntryReposiroty.save(entry);

		return entry;
	}

}
