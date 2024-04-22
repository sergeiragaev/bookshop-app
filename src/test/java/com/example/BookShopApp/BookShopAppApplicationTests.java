package com.example.BookShopApp;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookShopAppApplicationTests {

	@Value("${auth.secret}")
	String authSecret;

	private final BookShopAppApplication appApplication;

	@Autowired
	public BookShopAppApplicationTests(BookShopAppApplication appApplication) {
		this.appApplication = appApplication;
	}

	@Test
	void contextLoads() {
		assertNotNull(appApplication);
	}

	@Test
	void verifyAuthSecret(){
		assertThat(authSecret, Matchers.containsString("box"));
	}
}
