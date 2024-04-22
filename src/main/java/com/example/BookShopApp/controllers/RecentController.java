package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/books/recent")
@RequiredArgsConstructor
public class RecentController {
    private final BookService bookService;

    @GetMapping("")
    public String recentBookPage() {
        return "books/recent";
    }

    @GetMapping("/page")
    @ResponseBody
    public BooksPageDto getRecentBooksPage(@RequestParam(value = "from", required = false, defaultValue = "01.01.1900") String from,
                                           @RequestParam(value = "to", required = false, defaultValue = "31.12.2999") String to,
                                           @RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) throws Exception {
        return new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, limit));
    }

    @ModelAttribute("books")
    public List<BookDto> recentBooks() throws Exception {
        return bookService.getPageOfRecentBooks(0, 20);
    }
}
