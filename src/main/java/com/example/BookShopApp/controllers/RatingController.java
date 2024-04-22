package com.example.BookShopApp.controllers;

import com.example.BookShopApp.data.ApiResponse;
import com.example.BookShopApp.services.BooksRatingAndPopularityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingController {
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    @ResponseBody
    @PostMapping("/changeBookStatus/{slug}")
    public ApiResponse handleChangeBookStatus(
            @PathVariable("slug") String slug,
            @RequestParam("value") Integer value,
            @CookieValue(name = "ratings", required = false) String ratings,
            HttpServletResponse response) {
            return booksRatingAndPopularityService.setBookRating(slug, value, ratings, response);
    }

    @ResponseBody
    @PostMapping("/rateBookReview/{slug}/{reviewid}")
    public ApiResponse rateBookReview(
            @PathVariable("slug") String slug,
            @PathVariable("reviewid") String reviewId,
            @RequestParam("value") short value) throws Exception {
            return booksRatingAndPopularityService.setReviewRating(reviewId, value);
        }
}
