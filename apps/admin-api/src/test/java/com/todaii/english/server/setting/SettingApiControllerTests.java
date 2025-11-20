package com.todaii.english.server.setting;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Setting;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.enums.SettingCategory;
import com.todaii.english.shared.request.server.SettingRequest;

@WebMvcTest(controllers = SettingApiController.class)
@Import(TestSecurityConfig.class)
public class SettingApiControllerTests {
	private static final String END_POINT_PATH = "/api/v1/setting";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private SettingService settingService;

	// ---------- GET /smtp ----------

	@Test
	@DisplayName("GET /smtp - trả về 200 OK khi có dữ liệu")
	void testGetSmtpSettings_withData() throws Exception {
		List<Setting> mockSettings = List.of(
				Setting.builder().key("mail_host").value("smtp.office365.com")
						.settingCategory(SettingCategory.MAIL_SERVER).build(),
				Setting.builder().key("mail_port").value("587").settingCategory(SettingCategory.MAIL_SERVER).build());

		given(this.settingService.getSettingsByCategory(SettingCategory.MAIL_SERVER)).willReturn(mockSettings);

		mockMvc.perform(get(END_POINT_PATH + "/smtp")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].key").value("mail_host"))
				.andExpect(jsonPath("$[0].value").value("smtp.office365.com"))
				.andExpect(jsonPath("$[1].key").value("mail_port")).andExpect(jsonPath("$[1].value").value("587"));
	}

	@Test
	@DisplayName("GET /smtp - trả về 204 No Content khi không có dữ liệu")
	void testGetSmtpSettings_noData() throws Exception {
		given(settingService.getSettingsByCategory(SettingCategory.MAIL_SERVER)).willReturn(List.of());

		mockMvc.perform(get(END_POINT_PATH + "/smtp")).andExpect(status().isNoContent());
	}

	// ---------- PUT /smtp ----------

	@Test
	@DisplayName("PUT /smtp - trả về 200 OK khi update thành công")
	void testUpdateSmtpSettings_success() throws Exception {
		SettingRequest request = new SettingRequest(SettingCategory.MAIL_SERVER, Map.of("mail_host", "smtp.office365.com", "mail_port", "587"));

		List<Setting> updatedSettings = List.of(
				Setting.builder().key("mail_host").value("smtp.office365.com")
						.settingCategory(SettingCategory.MAIL_SERVER).build(),
				Setting.builder().key("mail_port").value("587").settingCategory(SettingCategory.MAIL_SERVER).build());

		given(settingService.updateSettings(request)).willReturn(updatedSettings);

		mockMvc.perform(put(END_POINT_PATH + "/smtp").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].key").value("mail_host"))
				.andExpect(jsonPath("$[0].value").value("smtp.office365.com"))
				.andExpect(jsonPath("$[1].key").value("mail_port")).andExpect(jsonPath("$[1].value").value("587"));
	}

	@Test
	@DisplayName("PUT /smtp - trả về 400 Bad Request khi body rỗng")
	void testUpdateSmtpSettings_invalidRequest() throws Exception {
		SettingRequest invalidRequest = new SettingRequest(SettingCategory.MAIL_SERVER, Map.of()); // rỗng

		mockMvc.perform(put(END_POINT_PATH + "/smtp").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /smtp - trả về 400 khi value rỗng")
	void testUpdateSmtpSettings_blankValue() throws Exception {
		// value = "" => NotBlank fail
		SettingRequest invalidRequest = new SettingRequest(SettingCategory.MAIL_SERVER, Map.of("mail_host", ""));

		mockMvc.perform(put(END_POINT_PATH + "/smtp").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /smtp - trả về 400 khi value null")
	void testUpdateSmtpSettings_nullValue() throws Exception {
		// value = null => NotBlank fail
		Map<String, String> map = new HashMap<>();
		map.put("mail_host", null);

		SettingRequest invalidRequest = new SettingRequest(SettingCategory.MAIL_SERVER, map);

		mockMvc.perform(put(END_POINT_PATH + "/smtp").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}

}
