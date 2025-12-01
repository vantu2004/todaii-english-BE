package com.todaii.english.server.dictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.core.entity.Video;
import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.server.article.ArticleRepository;
import com.todaii.english.server.event.EventService;
import com.todaii.english.server.video.VideoRepository;
import com.todaii.english.server.vocabulary.VocabDeckRepository;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.enums.EventType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.AIResponse;
import com.todaii.english.shared.response.DictionaryApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictionaryEntryService {
	private final DictionaryPort dictionaryPort;
	private final GeminiPort geminiPort;
	private final DictionaryEntryRepository dictionaryEntryRepository;
	private final ArticleRepository articleRepository;
	private final VideoRepository videoRepository;
	private final VocabDeckRepository vocabDeckRepository;
	private final ObjectMapper objectMapper;
	private final ModelMapper modelMapper;
	private final EventService eventService;

	public DictionaryApiResponse[] lookupWord(Long currentAdminId, String word) {
		DictionaryApiResponse[] response = dictionaryPort.lookupWord(word);

		// ko cần check response nữa vì nó đã tự ném lỗi
		eventService.logAdmin(currentAdminId, EventType.DICTIONARY_API, 1, Map.of("headword", word));

		return response;
	}

	public List<DictionaryEntry> createWordByGemini(Long currentAdminId, String word) throws Exception {
		List<DictionaryEntry> dictionaryEntries = dictionaryEntryRepository.findAllByHeadword(word);
		if (!dictionaryEntries.isEmpty()) {
			return dictionaryEntries;
		}
		// Gọi Free Dictionary API
		DictionaryApiResponse[] rawData = lookupWord(currentAdminId, word);
		String rawJson = objectMapper.writeValueAsString(rawData);

		// Gọi Gemini (luôn trả về mảng JSON)
		String prompt = String.format(Gemini.DICTIONARY_PROMPT, rawJson, word);

		AIResponse aiResponse = geminiPort.generateText(prompt);
		eventService.logAdmin(currentAdminId, EventType.AI_REQUEST, 1,
				Map.of("input_token", aiResponse.getInputToken(), "output_token", aiResponse.getOutputToken()));

		String rawResponseText = aiResponse.getText();

		// Parse mảng DTO
		DictionaryEntryDTO[] dtoArray = objectMapper.readValue(rawResponseText, DictionaryEntryDTO[].class);

		dictionaryEntries = Arrays.stream(dtoArray).map(this::toEntity).map(dictionaryEntryRepository::save).toList();

		return dictionaryEntries;
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

	@Deprecated
	public List<DictionaryEntry> findAll() {
		return dictionaryEntryRepository.findAll();
	}

	public Page<DictionaryEntry> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);
		Page<DictionaryEntry> entries = dictionaryEntryRepository.search(keyword, pageable);

		return entries;
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

	// muốn xóa 1 từ thì phải xóa các quan hệ xung quanh
	public void deleteWord(Long entryId) {
		DictionaryEntry entry = dictionaryEntryRepository.findById(entryId)
				.orElseThrow(() -> new BusinessException(404, "Word not found"));

		// Xóa quan hệ trong article
		List<Article> articles = articleRepository.findAllByWords_Id(entryId);
		for (Article article : articles) {
			article.getWords().remove(entry);
		}

		// Xóa quan hệ trong video
		List<Video> videos = videoRepository.findAllByWords_Id(entryId);
		for (Video video : videos) {
			video.getWords().remove(entry);
		}

		// Xóa quan hệ trong deck
		List<VocabDeck> decks = vocabDeckRepository.findAllByWords_Id(entryId);
		for (VocabDeck deck : decks) {
			deck.getWords().remove(entry);
		}

		// Lưu thay đổi
		articleRepository.saveAll(articles);
		videoRepository.saveAll(videos);
		vocabDeckRepository.saveAll(decks);

		// Cuối cùng xóa entry
		dictionaryEntryRepository.delete(entry);
	}

}
