package com.todaii.english.core.refresh_token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AdminRefreshTokenRepositoryTests {
	@Autowired
	private AdminRefreshTokenRepository adminRefreshTokenRepository;

	@Test
	void testFindByAdminEmail() {
		String email = "devvantu@gmail.com";

		List<AdminRefreshToken> adminRefreshTokens = this.adminRefreshTokenRepository.findByAdminEmail(email);
		assertThat(adminRefreshTokens).isNotEmpty();

		System.out.println(adminRefreshTokens);
	}
}
