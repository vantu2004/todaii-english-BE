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
public class UserRefreshTokenRepositoryTests {
	@Autowired
	private UserRefreshTokenRepository userRefreshTokenRepository;

	@Test
	public void testFindByUserEmail() {
		String email = "devvantu@gmail.com";

		List<UserRefreshToken> userRefreshTokens = this.userRefreshTokenRepository.findByUserEmail(email);
		assertThat(userRefreshTokens).isNotEmpty();

		System.out.println(userRefreshTokens);
	}

}
