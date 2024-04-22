package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.services.BookService;
import com.example.BookShopApp.services.BooksRatingAndPopularityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books/popular")
@RequiredArgsConstructor
public class PopularController {
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final BookService bookService;

    @GetMapping("")
    public String popularBookPage() {
        return "books/popular";
    }

    @GetMapping("/page")
    @ResponseBody
    public BooksPageDto getPopularBooksPage(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.fillBookDtoList(
                booksRatingAndPopularityService.getPageOfPopularBooks(offset, limit).getContent()));
    }

    @ModelAttribute("books")
    public List<BookDto> popularBooks(){
        return bookService.fillBookDtoList(
                booksRatingAndPopularityService.getPageOfPopularBooks(0, 20).getContent());
    }
}
