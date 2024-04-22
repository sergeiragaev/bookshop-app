package com.example.BookShopApp.services;

import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BooksRatingAndPopularityServiceTests {
    private Optional<String> postponedContents = Optional.empty();
    private Optional<String> cartContents = Optional.empty();

    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final BookRepository bookRepository;

    @Autowired
    public BooksRatingAndPopularityServiceTests(BooksRatingAndPopularityService booksRatingAndPopularityService, BookRepository bookRepository) {
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.bookRepository = bookRepository;
    }

    @Test
    void getRecommendedBooksNotAuthorizedNoCookieSet() {
        List<BookDto> bookDtoListNoCookieSet = booksRatingAndPopularityService.getPageOfRecommendedBooks(
                postponedContents, cartContents,0, 10000);
        assertNotNull(bookDtoListNoCookieSet);

        int currentRating = Integer.MAX_VALUE;
        for (BookDto bookDto : bookDtoListNoCookieSet){
            int rating = bookDto.getRating();
            Logger.getLogger(this.getClass().getSimpleName()).info(String.valueOf(rating));
            assertTrue(currentRating >= rating);
            currentRating = bookDto.getRating();
        }
    }

    @Test
    void getRecommendedBooksNotAuthorizedWithCartCookie() {

        cartContents = Optional.of(bookRepository.findById(1).get().getSlug());
        List<BookDto> bookDtoListWithCartCookie = booksRatingAndPopularityService.getPageOfRecommendedBooks(
                postponedContents, cartContents,0, 10000);

        assertNotNull(bookDtoListWithCartCookie);

        postponedContents = Optional.of(bookRepository.findById(bookRepository.findAll().size()).get().getSlug());
        List<BookDto> bookDtoListWithCartAndPostponedCookieSet = booksRatingAndPopularityService.getPageOfRecommendedBooks(
                postponedContents, cartContents,0, 10000);

        assertNotNull(bookDtoListWithCartAndPostponedCookieSet);
        assertNotEquals(bookDtoListWithCartAndPostponedCookieSet.size(),
                bookDtoListWithCartCookie.size());

        int previousRating = Integer.MAX_VALUE;
        for (BookDto bookDto : bookDtoListWithCartAndPostponedCookieSet){
            int rating = bookDto.getRating();
            Logger.getLogger(this.getClass().getSimpleName()).info(String.valueOf(rating));
            assertTrue(previousRating >= rating);
            previousRating = rating;
        }
    }

    @Test
    @WithUserDetails("user@gmail.com")
    void getRecommendedBooksAuthorizedCookieIgnored() {
        List<BookDto> bookDtoListNoCookieSet = booksRatingAndPopularityService.getPageOfRecommendedBooks(
                postponedContents, cartContents,0, 10000);
        assertNotNull(bookDtoListNoCookieSet);

        cartContents = Optional.of(bookRepository.findById(1).get().getSlug());
        List<BookDto> bookDtoListWithCartCookie = booksRatingAndPopularityService.getPageOfRecommendedBooks(
                postponedContents, cartContents,0, 10000);

        assertNotNull(bookDtoListWithCartCookie);
        assertEquals(bookDtoListNoCookieSet.size(),
                bookDtoListWithCartCookie.size());

        postponedContents = Optional.of(bookRepository.findById(bookRepository.findAll().size()).get().getSlug());
        List<BookDto> bookDtoListWithCartAndPostponedCookieSet = booksRatingAndPopularityService.getPageOfRecommendedBooks(
                postponedContents, cartContents,0, 100000);

        assertNotNull(bookDtoListWithCartAndPostponedCookieSet);
        assertEquals(bookDtoListNoCookieSet.size(),
                bookDtoListWithCartAndPostponedCookieSet.size());
        int previousRating = Integer.MAX_VALUE;
        for (BookDto bookDto : bookDtoListWithCartAndPostponedCookieSet){
            int rating = bookDto.getRating();
            Logger.getLogger(this.getClass().getSimpleName()).info(String.valueOf(rating));
            assertTrue(previousRating >= rating);
            previousRating = rating;
        }
    }

    @Test
    void getPopularBooks() {
        List<BookEntity> bookList =
                booksRatingAndPopularityService.getPageOfPopularBooks(0,10000).getContent() ;
        assertNotNull(bookList);
        assertNotEquals(0, bookList.size());

        float previousPopularity = Float.MAX_VALUE;
        for (BookEntity book : bookList){
            float popularity = Float.parseFloat(String.valueOf(bookRepository.countOfBoughtBooks(book.getId()) +
                    0.7 * bookRepository.countOfBooksInCart(book.getId()) +
                    0.4 * bookRepository.countOfPostponedBooks(book.getId())));
            Logger.getLogger(this.getClass().getSimpleName()).info(String.valueOf(popularity));
            assertTrue(previousPopularity >= popularity);
            previousPopularity = popularity;
        }
    }
}