package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.author.AuthorEntity;
import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.services.AuthorService;
import com.example.BookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorEntity>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "authors/index";
    }

    @GetMapping("/authors/{slug}")
    public String authorsPage(@PathVariable(value = "slug") String slug,
                             Model model){
        model.addAttribute("books", bookService.getPageOfBooksWithAuthor(slug, 0, 5));
        model.addAttribute("author", authorService.getAuthorBySlug(slug));
        model.addAttribute("refreshid", slug);
        return "/authors/slug";
    }
    @GetMapping("/books/author/{slug}")
    public String authorPage(@PathVariable(value = "slug") String slug,
                             Model model){
        model.addAttribute("books", bookService.getPageOfBooksWithAuthor(slug, 0, 5));
        model.addAttribute("author", authorService.getAuthorBySlug(slug));
        model.addAttribute("refreshid", slug);
        return "/books/author";
    }

    @GetMapping("/books/author/page/{slug}")
    @ResponseBody
    public BooksPageDto getTagBooksPage(@PathVariable(value = "slug") String slug,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageOfBooksWithAuthor(slug, offset, limit));
    }
}
