package com.todaii.english.server.vocabulary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.request.server.DeckRequest;

@WebMvcTest(VocabDeckApiController.class)
@Import(TestSecurityConfig.class)
class VocabDeckApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VocabDeckService vocabDeckService;

	@MockBean
	private VocabDeckGeneratorService vocabDeckGeneratorService;

	@Autowired
	private ObjectMapper objectMapper;

	// ============================================================
	// GET ALL DECKS
	// ============================================================
	@Deprecated
	@Test
	@DisplayName("GET /api/v1/vocab-deck → should return list of decks")
	void testGetAllDecks() throws Exception {
		VocabDeck deck = VocabDeck.builder().id(1L).name("Basic English").description("Common words").build();

		given(vocabDeckService.findAll()).willReturn(List.of(deck));

		mockMvc.perform(get("/api/v1/vocab-deck")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Basic English"));
	}

	// ============================================================
	// GET ALL DECKS (PAGED)
	// ============================================================
	@Test
	@DisplayName("GET /api/v1/vocab-deck (Paged) → should return paged list of decks")
	void testGetAllDecksPaged() throws Exception {
		VocabDeck deck = VocabDeck.builder().id(1L).name("Basic English").description("Common words").build();

		// Sử dụng PageImpl để mock Page<VocabDeck>
		Page<VocabDeck> page = new PageImpl<>(List.of(deck));

		// Mock service.findAllPaged (Lưu ý: ArgumentMatchers trong mock service)
		given(vocabDeckService.findAllPaged(any(Integer.class), any(Integer.class), any(String.class),
				any(String.class), any(String.class))).willReturn(page);

		mockMvc.perform(get("/api/v1/vocab-deck?page=1&size=10&keyword=Basic")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Basic English")).andExpect(jsonPath("$.page").value(1))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

	// ============================================================
	// GET DECKS BY GROUP ID
	// ============================================================
	@Test
	@DisplayName("GET /api/v1/vocab-deck/group/{groupId} → should return paged list filtered by group")
	void testGetDecksByGroupId() throws Exception {
		VocabDeck deck = VocabDeck.builder().id(2L).name("Grouped Deck").build();

		Page<VocabDeck> page = new PageImpl<>(List.of(deck));

		// Mock service.findByGroupId
		given(vocabDeckService.findByGroupId(eq(10L), any(Integer.class), any(Integer.class), any(String.class),
				any(String.class), any(String.class))).willReturn(page);

		mockMvc.perform(get("/api/v1/vocab-deck/group/10?keyword=Grouped")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Grouped Deck"))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	@DisplayName("GET /api/v1/vocab-deck/group/{groupId} → should return 400 Bad Request for invalid page param")
	void testGetDecksByGroupId_InvalidPage() throws Exception {
		// Kiểm tra validation @Min(1)
		mockMvc.perform(get("/api/v1/vocab-deck/group/10?page=0")).andExpect(status().isBadRequest());
	}

	// ============================================================
	// GET BY ID
	// ============================================================
	@Test
	@DisplayName("GET /api/v1/vocab-deck/{id} → should return deck by id")
	void testGetDeckById() throws Exception {
		VocabDeck deck = VocabDeck.builder().id(1L).name("Test Deck").description("For testing").build();

		given(vocabDeckService.findById(1L)).willReturn(deck);

		mockMvc.perform(get("/api/v1/vocab-deck/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test Deck"));
	}

	// ============================================================
	// CREATE DECK
	// ============================================================
	@Test
	@DisplayName("POST /api/v1/vocab-deck → should create new deck")
	void testCreateDeck() throws Exception {
		DeckRequest req = DeckRequest.builder().name("My Deck").description("Test Deck").cefrLevel(CefrLevel.A1)
				.groupIds(Set.of(1L, 2L)).build();
		VocabDeck deck = VocabDeck.builder().id(1L).name("My Deck").description("Test Desc").build();

		given(vocabDeckService.createDraftDeck(any(DeckRequest.class))).willReturn(deck);

		mockMvc.perform(post("/api/v1/vocab-deck").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("My Deck"));
	}

	// ============================================================
	// ADD WORD TO DECK
	// ============================================================
	@Test
	@DisplayName("POST /api/v1/vocab-deck/{deckId}/word/{wordId} → should add word")
	void testAddWordToDeck() throws Exception {
		DictionaryEntry entry = DictionaryEntry.builder().id(2L).headword("run").build();
		VocabDeck deck = VocabDeck.builder().id(1L).name("Deck A").words(Set.of(entry)).build();

		given(vocabDeckService.addWordToDeck(1L, 2L)).willReturn(deck);

		mockMvc.perform(post("/api/v1/vocab-deck/1/word/2")).andExpect(status().isOk())
				.andExpect(jsonPath("$.words[0].headword").value("run"));
	}

	// ============================================================
	// AUTO GENERATE WORDS
	// ============================================================
	@Test
	@DisplayName("POST /api/v1/vocab-deck/{deckId} → should auto generate words")
	void testAutoGenerateDeckWords() throws Exception {
		VocabDeck deck = VocabDeck.builder().id(1L).name("Auto Deck").build();

		given(vocabDeckGeneratorService.autoGenerateDeckWords(1L)).willReturn(deck);

		mockMvc.perform(post("/api/v1/vocab-deck/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Auto Deck"));
	}

	// ============================================================
	// UPDATE DECK
	// ============================================================
	@Test
	@DisplayName("PUT /api/v1/vocab-deck/{deckId} → should update deck")
	void testUpdateDeck() throws Exception {
		DeckRequest req = DeckRequest.builder().name("Updated").description("Desc").cefrLevel(CefrLevel.B2)
				.groupIds(Set.of(1L, 2L)).build();
		VocabDeck deck = VocabDeck.builder().id(1L).name("Updated").description("Desc").build();

		given(vocabDeckService.updateVocabDeck(eq(1L), any(DeckRequest.class))).willReturn(deck);

		mockMvc.perform(put("/api/v1/vocab-deck/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated"));
	}

	// ============================================================
	// DELETE WORD FROM DECK
	// ============================================================
	@Test
	@DisplayName("DELETE /api/v1/vocab-deck/{deckId}/word/{wordId} → should remove word")
	void testRemoveWordFromDeck() throws Exception {
		VocabDeck deck = VocabDeck.builder().id(1L).name("Deck A").build();

		given(vocabDeckService.removeWordFromDeck(1L, 2L)).willReturn(deck);

		mockMvc.perform(delete("/api/v1/vocab-deck/1/word/2")).andExpect(status().isOk());
	}

	// ============================================================
	// DELETE DECK
	// ============================================================
	@Test
	@DisplayName("DELETE /api/v1/vocab-deck/{deckId} → should delete deck")
	void testDeleteDeck() throws Exception {
		doNothing().when(vocabDeckService).deleteById(1L);

		mockMvc.perform(delete("/api/v1/vocab-deck/1")).andExpect(status().isNoContent());
	}
}
