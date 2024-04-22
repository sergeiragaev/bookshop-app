package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.dto.BooksPageDto;
import com.example.BookShopApp.model.tag.TagEntity;
import com.example.BookShopApp.services.BookService;
import com.example.BookShopApp.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagsController {
    private final TagService tagService;
    private final BookService bookService;

    @ModelAttribute("tags")
    public List<TagEntity> tagList(){
        return tagService.getTagData();
    }

    @GetMapping("/{slug}")
    public String tagsPage(@PathVariable(value = "slug") String slug,
                           Model model){
        model.addAttribute("books", bookService.getPageOfBooksWithTag(slug, 0, 20));
        model.addAttribute("tagName", tagService.getTagBySlug(slug).getName());
        model.addAttribute("refreshid", slug);
        return "/tags/index";
    }

    @GetMapping("/page")
    @ResponseBody
    public BooksPageDto getTagBooksPage(@RequestParam("slug") String slug,
                                            @RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageOfBooksWithTag(slug, offset, limit));
    }
}
