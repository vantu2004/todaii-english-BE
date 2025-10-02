package com.todaii.english.server.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository userRepository;

	@Test
	public void testGetAllUsers() {
		List<User> users = userRepository.findAll();
		
		assertThat(users).isNotEmpty();
		
		System.out.println(users);
	}

	@Test
	public void getUserById() {
		Optional<User> user = userRepository.findById(1L);

		assertThat(user).isPresent();

		System.out.println(user);
	}
}
