package com.example.BookShopApp.controllers;

import com.example.BookShopApp.errs.EmptySearchException;
import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.model.dto.SearchWordDto;
import com.example.BookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final BookService bookService;
    @GetMapping(value = {"", "/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                   Model model) throws Exception {
        if (searchWordDto != null && searchWordDto.getExample().length() > 1) {

            model.addAttribute("searchWordDto", searchWordDto);
            model.addAttribute("books", bookService.getPageOfGoogleBooksApiSearchResult(searchWordDto.getExample(), 0, 20));
            model.addAttribute("searchedBooksCount", bookService.getBooksWithTitle(searchWordDto.getExample()).size());
            return "/search/index";
        } else {
            throw new EmptySearchException("Search is possible only by 2 or more symbols...");
        }
    }

    @GetMapping("/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) throws Exception {
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit),
                bookService.getBooksWithTitle(searchWordDto.getExample()).size());
    }
}
