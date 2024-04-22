package com.example.BookShopApp.controllers;

import com.example.BookShopApp.data.ApiResponse;
import com.example.BookShopApp.services.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ApiService apiService;
    @ResponseBody
    @PostMapping("/bookReview")
    public ApiResponse postBookReview(
            @RequestBody() String body
    ) throws Exception {
        return apiService.postReview(body);
    }
}
