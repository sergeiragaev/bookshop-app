package com.example.BookShopApp.security.jwt;

import com.example.BookShopApp.security.BookstoreUserDetails;
import com.example.BookShopApp.security.BookstoreUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class JWTRequestFilterTest {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;

    @Autowired
    public JWTRequestFilterTest(BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Test
    void doFilterInternal() {
        String username = "user@gmail.com";
        String token = jwtUtil.generateToken(bookstoreUserDetailsService.loadUserByUsername(username));
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(username);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }
}