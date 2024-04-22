package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.model.genre.GenreEntity;
import com.example.BookShopApp.services.BookService;
import com.example.BookShopApp.services.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final BookService bookService;
    private final GenreService genreService;

    @GetMapping("")
    public String genresPage() {
        return "genres/index";
    }

    @ModelAttribute("genres")
    public List<GenreEntity> genreList(){
        return genreService.getGenreData();
    }

    @GetMapping("/{slug}")
    public String genresPage(@PathVariable(value = "slug") String slug,
                           Model model){
        model.addAttribute("books", bookService.getPageOfBooksWithGenre(slug, 0, 20));
        model.addAttribute("genre", genreService.getGenreBySlug(slug).getName());
        model.addAttribute("refreshid", slug);
        return "/genres/slug";
    }

    @GetMapping("/page/{slug}")
    @ResponseBody
    public BooksPageDto getGenreBooksPage(@PathVariable(value = "slug") String slug,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageOfBooksWithGenre(slug, offset, limit));
    }

}
