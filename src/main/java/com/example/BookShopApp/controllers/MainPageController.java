package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.model.tag.TagEntity;
import com.example.BookShopApp.services.BookService;
import com.example.BookShopApp.services.BooksRatingAndPopularityService;
import com.example.BookShopApp.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainPageController {
    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    private final TagService tagService;

    @ModelAttribute("recommended")
    public List<BookDto> recommendedBooks(@CookieValue(name = "postponedContents", required = false) Optional<String> postponedContents,
                                          @CookieValue(name = "cartContents", required = false) Optional<String> cartContents){
        return booksRatingAndPopularityService.getPageOfRecommendedBooks(postponedContents, cartContents, 0, 6);
    }

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getRecommendedBooksPage(@CookieValue(name = "postponedContents", required = false) Optional<String> postponedContents,
                                                @CookieValue(name = "cartContents", required = false) Optional<String> cartContents,
                                                @RequestParam("offset") Integer offset,
                                                @RequestParam("limit") Integer limit){
        return new BooksPageDto(booksRatingAndPopularityService.getPageOfRecommendedBooks(postponedContents, cartContents, offset, limit));
    }


    @ModelAttribute("recent")
    public List<BookDto> recentBooks() throws Exception {
        return bookService.getPageOfRecentBooks("", "", 0, 6);
    }

    @ModelAttribute("popular")
    public List<BookDto> popularBooks(){
        return bookService.fillBookDtoList(booksRatingAndPopularityService.getPageOfPopularBooks(0, 6).getContent());
    }

    @ModelAttribute("tags")
    public List<TagEntity> tagList(){
        return tagService.getTagData();
    }
}
