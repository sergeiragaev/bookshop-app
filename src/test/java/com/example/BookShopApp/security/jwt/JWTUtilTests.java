package com.example.BookShopApp.security.jwt;

import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.security.BookstoreUserDetailsService;
import com.example.BookShopApp.security.BookstoreUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class JWTUtilTests {

    private final JWTUtil jwtUtil;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final BookstoreUserRepository bookstoreUserRepository;
    private String token;

    @Autowired
    public JWTUtilTests(JWTUtil jwtUtil, BookstoreUserDetailsService bookstoreUserDetailsService, BookstoreUserRepository bookstoreUserRepository) {
        this.jwtUtil = jwtUtil;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @BeforeEach
    void setUp() {
        BookstoreUser bookstoreUser = new BookstoreUser();
        bookstoreUser.setEmail("test@mail.org");
        bookstoreUser.setName("Tester");
        bookstoreUser.setPassword("qwerty");
        bookstoreUser.setPhone("9139248434");
        bookstoreUserRepository.save(bookstoreUser);
        token = jwtUtil.generateToken(bookstoreUserDetailsService.loadUserByUsername(bookstoreUser.getEmail()));
    }

    @AfterEach
    void tearDown() {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail("test@mail.org");
        bookstoreUserRepository.delete(bookstoreUser);
    }

    @Test
    void generateToken() {
        assertNotNull(token);
    }

    @Test
    void extractUserName() {
        assertEquals(jwtUtil.extractUserName(token), "Tester");
    }

    @Test
    void extractExpiration() {
        assertNotNull(jwtUtil.extractExpiration(token));
    }

    @Test
    void isTokenExpired() {
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void validateToken() {
        assertTrue(jwtUtil.validateToken(token, bookstoreUserDetailsService.loadUserByUsername("Tester")));
    }
}