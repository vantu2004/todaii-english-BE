package com.todaii.english.server.dictionary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.shared.enums.PartOfSpeech;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class DictionaryEntryRepositoryTests {

	@Autowired
	private DictionaryEntryRepository dictionaryEntryRepository;

	/**
	 * Helper method để tạo sẵn 1 DictionaryEntry trong DB
	 */
	private DictionaryEntry createSampleEntry() {
		DictionarySense sense = DictionarySense.builder().pos(PartOfSpeech.noun).meaning("cuộc chạy, hành trình")
				.definition("an act of running").example("He went for a morning run.").build();

		DictionaryEntry entry = DictionaryEntry.builder().headword("run").ipa("/rʌn/")
				.audioUrl("https://cdn.todaii.vn/audio/run.mp3").build();

		sense.setEntry(entry);
		entry.getSenses().add(sense);

		return dictionaryEntryRepository.save(entry);
	}

	// ============================================================
	// 🧩 CREATE
	// ============================================================

	@Test
	@DisplayName("CREATE - should save a new DictionaryEntry successfully")
	void testCreateEntry() {
		DictionaryEntry entry = createSampleEntry();

		assertThat(entry.getId()).isNotNull();
		assertThat(entry.getHeadword()).isEqualTo("run");
		assertThat(entry.getSenses()).hasSize(1);
	}

	// ============================================================
	// 🧩 READ
	// ============================================================

	@Test
	@DisplayName("READ - should find entry by ID")
	void testFindById() {
		DictionaryEntry saved = createSampleEntry();
		Optional<DictionaryEntry> found = dictionaryEntryRepository.findById(saved.getId());

		assertThat(found).isPresent();
		assertThat(found.get().getHeadword()).isEqualTo("run");
		assertThat(found.get().getSenses()).isNotEmpty();
	}

	@Test
	@DisplayName("READ - should find entry by headword in ignore case")
	void testFindByHeadwordInIgnoreCase() {
		Set<String> headwords = Set.of("RUN", "COMMON");
		List<DictionaryEntry> results = dictionaryEntryRepository.findByHeadwordInIgnoreCase(headwords);

		assertThat(results).isNotEmpty();
		assertThat(results.get(0).getHeadword()).isEqualTo("run");
		assertThat(results.get(1).getHeadword()).isEqualTo("common");
	}

	// ============================================================
	// 🧩 UPDATE
	// ============================================================

	@Test
	@DisplayName("UPDATE - should update IPA and audioUrl successfully")
	void testUpdateEntry() {
		DictionaryEntry entry = createSampleEntry();

		entry.setIpa("/run/");
		entry.setAudioUrl("https://example.com/audio.mp3");
		dictionaryEntryRepository.save(entry);

		DictionaryEntry updated = dictionaryEntryRepository.findById(entry.getId()).orElseThrow();

		assertThat(updated.getIpa()).isEqualTo("/run/");
		assertThat(updated.getAudioUrl()).contains("example.com");
	}

	// ============================================================
	// 🧩 EXISTS
	// ============================================================

	@Test
	@DisplayName("EXISTS - should return true when headword exists")
	void testExistsByHeadword() {
		createSampleEntry();

		boolean exists = dictionaryEntryRepository.existsByHeadword("run");

		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("EXISTS - should return false when headword does not exist")
	void testExistsByHeadword_false() {
		boolean exists = dictionaryEntryRepository.existsByHeadword("unknownword");
		assertThat(exists).isFalse();
	}

	// ============================================================
	// 🧩 DELETE
	// ============================================================

	@Test
	@DisplayName("DELETE - should delete entry and its senses (cascade)")
	void testDeleteEntry() {
		DictionaryEntry entry = createSampleEntry();

		Long id = entry.getId();
		dictionaryEntryRepository.deleteById(id);

		Optional<DictionaryEntry> found = dictionaryEntryRepository.findById(id);

		assertThat(found).isEmpty();
	}

	// ============================================================
	// 🧩 SEARCH
	// ============================================================

	@Test
	@DisplayName("SEARCH - should return all when keyword is null")
	void testSearch_keywordNull() {
		Page<DictionaryEntry> page = dictionaryEntryRepository.search(null, PageRequest.of(0, 10));
		assertThat(page).isNotNull();
	}

	@Test
	@DisplayName("SEARCH - should return all when keyword is null")
	void testSearch_keywordNotNull() {
		Page<DictionaryEntry> page = dictionaryEntryRepository.search("template", PageRequest.of(0, 10));
		assertThat(page).isNotNull();
	}

}
