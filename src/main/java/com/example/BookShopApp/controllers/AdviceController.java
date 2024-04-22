package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.dto.SearchWordDto;
import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.services.BookService;
import com.example.BookShopApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;
@ControllerAdvice
@RequiredArgsConstructor
public class AdviceController {
    private final BookService bookService;
    private final UserService userService;
    @ModelAttribute("postponedCount")
    public Integer postponedCount(@CookieValue(name = "postponedContents", required = false) Optional<String> postponedContents){
        return bookService.getPostponedCount(postponedContents);
    }
    @ModelAttribute("cartCount")
    public Integer cartCount(@CookieValue(name = "cartContents", required = false) Optional<String> cartContents){
        return bookService.getCartCount(cartContents);
    }
    @ModelAttribute("booksCount")
    public Integer booksCount(){
        return bookService.getBooksCount();
    }
    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }
    @ModelAttribute("status")
    public String status(){
        return userService.getStatus();
    }
    @ModelAttribute("curUsr")
    public BookstoreUser curUsr(){
        return userService.getCurUsr();
    }
}
