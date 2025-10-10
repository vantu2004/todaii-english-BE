package com.todaii.english.server.dictionary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionarySense;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.dto.DictionarySenseDTO;
import com.todaii.english.shared.enums.PartOfSpeech;
import com.todaii.english.shared.exceptions.BusinessException;

@WebMvcTest(controllers = DictionaryEntryApiController.class)
@Import(TestSecurityConfig.class)
public class DictionaryEntryApiControllerTests {

	private static final String END_POINT_PATH = "/api/v1/dictionary";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private DictionaryEntryService dictionaryService;

	// ============================
	// 🔹 MOCK DATA
	// ============================
	private DictionaryEntry createSampleEntry() {
		DictionarySense sense = DictionarySense.builder().id(1L).pos(PartOfSpeech.verb).meaning("chạy")
				.definition("to move swiftly on foot").example("He runs fast.").build();

		DictionaryEntry entry = DictionaryEntry.builder().id(10L).headword("run").ipa("/rʌn/")
				.audioUrl("https://cdn.todaii.vn/audio/run.mp3").build();
		entry.getSenses().add(sense);
		return entry;
	}

	private DictionaryEntryDTO createSampleDTO() {
		DictionarySenseDTO senseDTO = new DictionarySenseDTO();
		senseDTO.setPos("VERB");
		senseDTO.setMeaning("chạy");
		senseDTO.setDefinition("to move swiftly");
		senseDTO.setExample("He runs fast.");
		senseDTO.setSynonyms(List.of("dash", "sprint"));
		senseDTO.setCollocations(List.of("run quickly"));

		DictionaryEntryDTO dto = new DictionaryEntryDTO();
		dto.setHeadword("run");
		dto.setIpa("/rʌn/");
		dto.setAudioUrl("https://cdn.todaii.vn/audio/run.mp3");
		dto.setSenses(Set.of(senseDTO));

		return dto;
	}

	// ============================
	// 🔹 GET ALL
	// ============================
	@Test
	@DisplayName("GET /api/v1/dictionary → 200 OK")
	void testGetAllWords_ok() throws Exception {
		when(dictionaryService.findAll()).thenReturn(List.of(createSampleEntry()));

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].headword").value("run")).andExpect(jsonPath("$[0].ipa").value("/rʌn/"));
	}

	@Test
	@DisplayName("GET /api/v1/dictionary → 204 No Content")
	void testGetAllWords_noContent() throws Exception {
		when(dictionaryService.findAll()).thenReturn(List.of());

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent());
	}

	// ============================
	// 🔹 GET BY ID
	// ============================
	@Test
	@DisplayName("GET /api/v1/dictionary/{id} → 200 OK")
	void testGetWord_ok() throws Exception {
		when(dictionaryService.findById(10L)).thenReturn(createSampleEntry());

		mockMvc.perform(get(END_POINT_PATH + "/10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.headword").value("run"))
				.andExpect(jsonPath("$.senses[0].meaning").value("chạy"));
	}

	@Test
	@DisplayName("GET /api/v1/dictionary/{id} → 404 Not Found")
	void testGetWord_notFound() throws Exception {
		when(dictionaryService.findById(99L)).thenThrow(new BusinessException(404, "Word not found"));

		mockMvc.perform(get(END_POINT_PATH + "/99")).andExpect(status().isNotFound());
	}

	// ============================
	// 🔹 CREATE
	// ============================
	@Test
	@DisplayName("POST /api/v1/dictionary → 201 Created")
	void testCreateWord_ok() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		DictionaryEntry saved = createSampleEntry();

		when(dictionaryService.createWord(any(DictionaryEntryDTO.class))).thenReturn(saved);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.headword").value("run")).andExpect(jsonPath("$.ipa").value("/rʌn/"));
	}

	@Test
	@DisplayName("POST /api/v1/dictionary → 409 Conflict when already exists")
	void testCreateWord_conflict() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		when(dictionaryService.createWord(any(DictionaryEntryDTO.class)))
				.thenThrow(new BusinessException(409, "already exists"));

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isConflict());
	}

	// ============================
	// 🔹 UPDATE
	// ============================
	@Test
	@DisplayName("PUT /api/v1/dictionary/{id} → 200 OK")
	void testUpdateWord_ok() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		DictionaryEntry updated = createSampleEntry();
		updated.setIpa("/run/");

		when(dictionaryService.updateWord(eq(10L), any(DictionaryEntryDTO.class))).thenReturn(updated);

		mockMvc.perform(put(END_POINT_PATH + "/10").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.ipa").value("/run/"));
	}

	@Test
	@DisplayName("PUT /api/v1/dictionary/{id} → 404 Not Found")
	void testUpdateWord_notFound() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		when(dictionaryService.updateWord(eq(10L), any(DictionaryEntryDTO.class)))
				.thenThrow(new BusinessException(404, "not found"));

		mockMvc.perform(put(END_POINT_PATH + "/10").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isNotFound());
	}

	// ============================
	// 🔹 DELETE
	// ============================
	@Test
	@DisplayName("DELETE /api/v1/dictionary/{id} → 204 No Content")
	void testDeleteWord_ok() throws Exception {
		doNothing().when(dictionaryService).deleteWord(10L);

		mockMvc.perform(delete(END_POINT_PATH + "/10")).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /api/v1/dictionary/{id} → 404 Not Found")
	void testDeleteWord_notFound() throws Exception {
		// Giả lập khi gọi deleteWord(99L) sẽ ném BusinessException
		doThrow(new BusinessException(404, "Word not found")).when(dictionaryService).deleteWord(99L);

		mockMvc.perform(delete(END_POINT_PATH + "/99")).andExpect(status().isNotFound());
	}

	// ============================
	// 🔹 GEMINI CREATE
	// ============================
	@Test
	@DisplayName("GET /api/v1/dictionary/gemini?word=run → 200 OK")
	void testCreateWordByGemini_ok() throws Exception {
		when(dictionaryService.createWordByGemini("run")).thenReturn(List.of(createSampleEntry()));

		mockMvc.perform(get(END_POINT_PATH + "/gemini").param("word", "run")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].headword").value("run"));
	}

	@Test
	@DisplayName("GET /api/v1/dictionary/gemini?word=run → 409 Conflict if exists")
	void testCreateWordByGemini_conflict() throws Exception {
		when(dictionaryService.createWordByGemini("run")).thenThrow(new BusinessException(409, "already exists"));

		mockMvc.perform(get(END_POINT_PATH + "/gemini").param("word", "run")).andExpect(status().isConflict());
	}

	// ============================================================
	// 🧩 VALIDATION TESTS
	// ============================================================

	@Test
	@DisplayName("POST /api/v1/dictionary → 400 Bad Request when headword is blank")
	void testCreateWord_validation_blankHeadword() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		dto.setHeadword(""); // invalid

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/dictionary → 400 Bad Request when audioUrl is invalid")
	void testCreateWord_validation_invalidAudioUrl() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		dto.setAudioUrl("invalid-url"); // violates @URL

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/dictionary → 400 Bad Request when senses missing")
	void testCreateWord_validation_missingSenses() throws Exception {
		DictionaryEntryDTO dto = createSampleDTO();
		DictionarySenseDTO sense = dto.getSenses().iterator().next();
		sense.setPos(""); // giả lập dữ liệu thiếu để test validation

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("GET /api/v1/dictionary/gemini → 400 Bad Request when word param is empty")
	void testGemini_validation_emptyParam() throws Exception {
		mockMvc.perform(get(END_POINT_PATH + "/gemini").param("word", "")).andExpect(status().isBadRequest());
	}

}
