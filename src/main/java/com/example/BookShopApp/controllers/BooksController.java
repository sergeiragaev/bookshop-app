package com.example.BookShopApp.controllers;

import com.example.BookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BooksController {

     private final BookService bookService;

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model){
        return bookService.getBookSlugPage(model, slug);
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@PathVariable("slug") String slug, @RequestParam("file")MultipartFile file) throws IOException {
        bookService.saveNewBookImage(file, slug);
        return ("redirect:/books/" + slug);
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {
        return bookService.downloadFile(hash);
    }
}
