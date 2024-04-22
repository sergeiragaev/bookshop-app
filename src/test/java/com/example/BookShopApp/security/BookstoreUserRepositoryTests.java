package com.example.BookShopApp.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRepositoryTests {
    private final BookstoreUserRepository bookstoreUserRepository;
    @Autowired
    public BookstoreUserRepositoryTests(BookstoreUserRepository bookstoreUserRepository) {
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Test
    public void testAddNewUser(){
        BookstoreUser user = new BookstoreUser();
        user.setPassword("1234567");
        user.setPhone("9139019999");
        user.setName("Test user");
        user.setEmail("test@testsite.org");

        assertNotNull(bookstoreUserRepository.save(user));
    }
}