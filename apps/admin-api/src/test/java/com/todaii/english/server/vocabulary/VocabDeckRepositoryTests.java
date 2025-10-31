package com.todaii.english.server.vocabulary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.shared.enums.CefrLevel;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true) // rollback sau mỗi test để không ảnh hưởng DB thật
public class VocabDeckRepositoryTests {
	@Autowired
	private VocabDeckRepository vocabDeckRepository;

	@Test
	@DisplayName("CREATE - Lưu deck mới vào DB")
	public void testCreateDeck() {
		VocabDeck deck = new VocabDeck();
		deck.setName("Basic English Deck");
		deck.setDescription("A simple deck for beginner level");
		deck.setCefrLevel(CefrLevel.A1);

		VocabDeck savedDeck = vocabDeckRepository.save(deck);

		assertThat(savedDeck).isNotNull();
		assertThat(savedDeck.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("READ - Lấy toàn bộ danh sách deck")
	public void testFindAllDecks() {
		List<VocabDeck> decks = vocabDeckRepository.findAll();

		assertThat(decks).isNotNull();
		assertThat(decks.size()).isGreaterThanOrEqualTo(0);
	}

	@Test
	@DisplayName("READ - Tìm deck theo ID")
	public void testFindById() {
		// giả định ID 1 tồn tại sẵn trong DB test
		Optional<VocabDeck> result = vocabDeckRepository.findById(3L);

		assertThat(result).isPresent();
		result.ifPresent(deck -> System.out.println("Found deck: " + deck.getName()));
	}

	@Test
	@DisplayName("UPDATE - Cập nhật thông tin deck")
	public void testUpdateDeck() {
		// tạo deck mới
		VocabDeck deck = new VocabDeck();
		deck.setName("Update Test Deck");
		deck.setDescription("Before update");
		deck.setCefrLevel(CefrLevel.A2);
		VocabDeck saved = vocabDeckRepository.save(deck);

		// cập nhật lại description
		saved.setDescription("After update");
		VocabDeck updated = vocabDeckRepository.save(saved);

		assertThat(updated.getDescription()).isEqualTo("After update");
	}

	@Test
	@DisplayName("DELETE - Xóa deck theo ID")
	public void testDeleteDeck() {
		VocabDeck deck = new VocabDeck();
		deck.setName("Temp Deck");
		deck.setDescription("To be deleted");
		deck.setCefrLevel(CefrLevel.B1);
		VocabDeck saved = vocabDeckRepository.save(deck);

		Long id = saved.getId();
		vocabDeckRepository.deleteById(id);

		Optional<VocabDeck> result = vocabDeckRepository.findById(id);
		assertThat(result).isEmpty();
	}
}
