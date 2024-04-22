package com.example.BookShopApp.controllers;

import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    @ModelAttribute(name = "bookcart")
    public List<BookEntity> bookCart(){
        return new ArrayList<>();
    }

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    Model model){
        cartService.setCartContents(cartContents, model);
        return "cart";
    }

    @PostMapping("/remove/{slug}")
    public String handleRemoveBookFromCartRequest(
            @PathVariable("slug") String slug,
            @CookieValue(name = "cartContents", required = false) String cartContents,
            HttpServletResponse response, Model model){
        cartService.removeFromCartContents(slug, cartContents, response, model);
        return "redirect:/cart";
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(
            @PathVariable("slug") String slug,
            @CookieValue(name = "cartContents", required = false) String cartContents,
            HttpServletResponse response, Model model){
        cartService.addToCartContents(slug, cartContents, response, model);
        return "redirect:/books/" + slug;
    }
}
