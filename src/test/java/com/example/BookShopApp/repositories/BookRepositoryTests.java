package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.book.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTests {

    private final BookRepository bookRepository;

    @Autowired
    public BookRepositoryTests(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void findBookEntitiesByTitleContaining() {
        String token = "ora";
        List<BookEntity> bookListByTitleContaining = bookRepository.findBookEntitiesByTitleContaining(token);

        assertNotNull(bookListByTitleContaining);
        assertFalse(bookListByTitleContaining.isEmpty());

        for (BookEntity book : bookListByTitleContaining){
            Logger.getLogger(this.getClass().getSimpleName()).info(book.getTitle());
            assertTrue(book.getTitle().contains(token));
        }
    }

    @Test
    void getBestsellers() {
        List<BookEntity> bestSellersBooks = bookRepository.getBestsellers();
        assertNotNull(bestSellersBooks);
        assertFalse(bestSellersBooks.isEmpty());
        assertTrue(bestSellersBooks.size() > 1);
    }

    @Test
    void getBookEntitiesWithMaxDiscount() {
        List<BookEntity> booksWithMaxDiscount = bookRepository.getBookEntitiesWithMaxDiscount();
        assertNotNull(booksWithMaxDiscount);
        assertFalse(booksWithMaxDiscount.isEmpty());

        for (BookEntity book : booksWithMaxDiscount){
            Logger.getLogger(this.getClass().getSimpleName()).info(book.getDiscount().toString());
            assertTrue(book.getDiscount()>0);
        }
    }
}