package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.services.PostponedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/postponed")
@RequiredArgsConstructor
public class PostponedController {
    private final PostponedService postponedService;

    @ModelAttribute(name = "bookPostponed")
    public List<BookEntity> bookPostponed(){
        return new ArrayList<>();
    }

    @GetMapping("")
    public String handlePostponedRequest(@CookieValue(value = "postponedContents", required = false) String postponedContents,
                                    Model model){
        postponedService.setPostponedContents(postponedContents, model);
        return "postponed";
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(
            @PathVariable("slug") String slug,
            @CookieValue(name = "postponedContents", required = false) String postponedContents,
            HttpServletResponse response, Model model){
        postponedService.addToPostponedContents(slug, postponedContents, response, model);
        return "redirect:/books/" + slug;
    }

    @PostMapping("/remove/{slug}")
    public String handleRemoveBookFromPostponedRequest(
            @PathVariable("slug") String slug,
            @CookieValue(name = "postponedContents", required = false) String postponedContents,
            HttpServletResponse response, Model model){
        postponedService.removeFromPostponedContents(slug, postponedContents, response, model);
        return "redirect:/postponed";
    }
}
