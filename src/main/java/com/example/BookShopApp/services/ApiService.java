package com.example.BookShopApp.services;

import com.example.BookShopApp.annotations.WarningExceptionsLoggable;
import com.example.BookShopApp.data.ApiResponse;
import com.example.BookShopApp.errs.BookStoreApiShortReviewException;
import com.example.BookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.BookShopApp.errs.BookstoreJwtException;
import com.example.BookShopApp.errs.UserReviewAlreadyExistsException;
import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.model.book.review.BookReviewEntity;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.model.user.UserEntity;
import com.example.BookShopApp.repositories.BookReviewRepository;
import com.example.BookShopApp.repositories.UserRepository;
import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.security.BookstoreUserRegister;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final BookService bookService;
    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;
    private final BookstoreUserRegister userRegister;

    public ApiResponse<BookDto> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        ApiResponse<BookDto> response = new ApiResponse<>();
        List<BookDto> data = bookService.getBooksWithTitle(title);
        response.setDebugMessage("successful request");
        response.setError("data size: " + data.size() + " elements");
        response.setTimeStamp(LocalDateTime.now());
        response.setData(data);
        return response;
    }

    public List<BookDto> getBooksWithPriceBetween(Integer min, Integer max) {
        return bookService.getBooksWithPriceBetween(min, max);
    }

    public List<BookDto> getBooksWithMaxDiscount() {
        return bookService.getBooksWithMaxDiscount();
    }

    public List<BookDto> getBestsellers() {
        return bookService.getBestsellers();
    }

    @WarningExceptionsLoggable
    public ApiResponse postReview(String body) throws Exception {
        BookstoreUser user;
        try {
            user = (BookstoreUser) userRegister.getCurrentUser();
        } catch (Exception e){
            throw new BookstoreJwtException("Отзывы могут оставлять только авторизованные пользователи");
        }
        JSONObject jsonObject = new JSONObject(body);
        String slug = jsonObject.get("bookId").toString();
        String text = jsonObject.get("text").toString();
        if (text.length() < 50){
            throw new BookStoreApiShortReviewException("Отзыв слишком короткий. Напишите, пожалуйста, более развёрнутый отзыв");
        } else {
            if (userRepository.findById(user.getId()).isPresent()) {
                BookEntity book = bookService.getBookBySlug(slug);
                if (book == null){
                    throw new BookstoreApiWrongParameterException("Книга для отзыва не найдена");
                }
                UserEntity userEntity = userRepository.findById(user.getId()).get();
                for (BookReviewEntity review : book.getReviews()) {
                    if (review.getUser() == userEntity){
                        throw new UserReviewAlreadyExistsException("Пользователь уже оставлял отзыв о данной книге");
                    }
                }
                BookReviewEntity bookReview = new BookReviewEntity();
                bookReview.setBook(book);
                bookReview.setTime(LocalDateTime.now());
                bookReview.setText(text);
                bookReview.setUser(userEntity);
                bookReviewRepository.save(bookReview);
            } else {
                throw new UsernameNotFoundException("Пользователь не найден");
            }
        }
        ApiResponse<BookDto> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        return response;
    }
}
