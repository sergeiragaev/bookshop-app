package com.example.BookShopApp.services;

import com.example.BookShopApp.annotations.WarningExceptionsLoggable;
import com.example.BookShopApp.data.ApiResponse;
import com.example.BookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.BookShopApp.errs.BookstoreJwtException;
import com.example.BookShopApp.errs.UserAlreadyRateReviewException;
import com.example.BookShopApp.errs.UserCannotRateOwnReviewException;
import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.model.book.RatingEntity;
import com.example.BookShopApp.model.book.review.BookReviewEntity;
import com.example.BookShopApp.model.book.review.BookReviewLikeEntity;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.model.user.UserEntity;
import com.example.BookShopApp.repositories.*;
import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.security.BookstoreUserRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BooksRatingAndPopularityService {
    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final UserRepository userRepository;
    private final BookstoreUserRegister userRegister;
    private final BookService bookService;

    public Page<BookEntity> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.getPopularBookEntities(nextPage);
    }

    public ApiResponse setBookRating(String slug, Integer value, String ratings, HttpServletResponse response) {
        boolean haveToAddRating = false;
        ApiResponse<BookDto> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.OK.value());
        if (ratings == null || ratings.equals("")) {
            Cookie cookie = new Cookie("ratings", slug);
            cookie.setPath("/rating");
            response.addCookie(cookie);
            haveToAddRating = true;
        } else if (!ratings.contains(slug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(ratings).add(slug);
            Cookie cookie = new Cookie("ratings", stringJoiner.toString());
            cookie.setPath("/rating");
            response.addCookie(cookie);
            haveToAddRating = true;
        }
        if (haveToAddRating) {
            BookEntity book = bookRepository.findBookEntityBySlug(slug);
            RatingEntity rating = new RatingEntity();
            rating.setId(ratingRepository.findAll().size());
            rating.setValue(value);
            rating.setBookId(book.getId());
            ratingRepository.save(rating);
            apiResponse.setResult(true);
        } else {
            apiResponse.setResult(false);
        }
        return apiResponse;
    }
    @WarningExceptionsLoggable
    public ApiResponse setReviewRating(String reviewId, short value) throws Exception {
        if (value == 0){
            throw new BookstoreApiWrongParameterException("Оценка не может быть равна нулю");
        }
        Optional<BookReviewEntity> optionalBookReview = bookReviewRepository.findById(Integer.parseInt(reviewId));
        if (optionalBookReview.isPresent()) {
            UserEntity user;
            try {
                user = userRepository.findById(
                        ((BookstoreUser) userRegister.getCurrentUser()).getId()).get();
            } catch (Exception e) {
                throw new BookstoreJwtException("Отзывы могут оценивать только авторизованные пользователи");
            }
            if (optionalBookReview.get().getUser() == user ){
                throw new UserCannotRateOwnReviewException("Пользователь не может оценивать свой отзыв");
            }
            BookReviewEntity bookReview = optionalBookReview.get();
            if (! bookReviewLikeRepository.existsByUserAndReview(user, bookReview)) {
                BookReviewLikeEntity like = new BookReviewLikeEntity();
                like.setValue(value);
                like.setReview(bookReview);
                like.setTime(LocalDateTime.now());
                like.setUser(user);
                bookReviewLikeRepository.save(like);
            } else {
                throw new UserAlreadyRateReviewException("Пользователь уже оценивал данный отзыв");
            }
        } else {
            throw new BookstoreApiWrongParameterException("Отзыв не найден");
        }

        ApiResponse<BookDto> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    public List<BookDto> getPageOfRecommendedBooks(Optional<String> postponedContents,
                                                   Optional<String> cartContents,
                                                   Integer offset, Integer limit){
        List<Integer> bookIdList = new ArrayList<>();
        try {
            UserEntity user = userRepository.findById(
                    ((BookstoreUser) userRegister.getCurrentUser()).getId()).get();
            bookIdList.addAll(bookRepository.findBookEntitiesByUser(user.getId()).stream().map(BookEntity::getId).toList());
        } catch (Exception e) {
            if (postponedContents.isPresent()) {
                Collection<String> cookieSlugs = Arrays.stream(postponedContents.get().split("/")).toList();
                List<BookEntity> booksFromCookiesSlugs = bookRepository.findBookEntitiesBySlugIn(cookieSlugs);
                bookIdList.addAll(booksFromCookiesSlugs.stream().map(BookEntity::getId).toList());
            }
            if (cartContents.isPresent()) {
                Collection<String> cookieSlugs = Arrays.stream(cartContents.get().split("/")).toList();
                List<BookEntity> booksFromCookiesSlugs = bookRepository.findBookEntitiesBySlugIn(cookieSlugs);
                bookIdList.addAll(booksFromCookiesSlugs.stream().map(BookEntity::getId).toList());
            }
        }

        Pageable nextPage = PageRequest.of(offset, limit);
        if (bookIdList.size() > 0) {
            return bookService.fillBookDtoList(bookRepository.getRecommendedBookEntities(nextPage, bookIdList).getContent());
        } else {
            return bookService.fillBookDtoList(bookRepository.getRecommendedBookEntities(nextPage).getContent());
        }
    }
}
