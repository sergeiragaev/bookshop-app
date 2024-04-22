package com.example.BookShopApp.controllers;

import com.example.BookShopApp.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class CartControllerTests {
    private final MockMvc mockMvc;
    private final BookRepository bookRepository;
    @Autowired
    public CartControllerTests(MockMvc mockMvc, BookRepository bookRepository) {
        this.mockMvc = mockMvc;
        this.bookRepository = bookRepository;
    }
    @Test
    public void removeFromCartTest() throws Exception {
        String slug = bookRepository.findById(1).get().getSlug();
        mockMvc.perform(post("/cart/remove/" + slug))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    public void addToCartTest() throws Exception {
        String slug = bookRepository.findById(1).get().getSlug();
        mockMvc.perform(post("/cart/changeBookStatus/" + slug))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + slug));
    }
}