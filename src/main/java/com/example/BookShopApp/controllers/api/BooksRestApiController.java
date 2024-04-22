package com.example.BookShopApp.controllers.api;

import com.example.BookShopApp.data.ApiResponse;
import com.example.BookShopApp.errs.*;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.services.ApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(description = "Book's data accessed by API")
@RequiredArgsConstructor
public class BooksRestApiController {
    private final ApiService apiService;

    @GetMapping("/books/by-title")
    @ApiOperation("get books by title")
    public ResponseEntity<ApiResponse<BookDto>> getBooksByTitle(@RequestParam("title") String title) throws BookstoreApiWrongParameterException {
        return ResponseEntity.ok(apiService.getBooksByTitle(title));
    }

    @GetMapping("/books/by-price-range")
    @ApiOperation("get books by price between min and max")
    public ResponseEntity<List<BookDto>> getBooksByPriceRange(@RequestParam("min") Integer min, @RequestParam("max") Integer max) {
        return ResponseEntity.ok(apiService.getBooksWithPriceBetween(min, max));
    }

    @GetMapping("/books/with-max-discount")
    @ApiOperation("get books with max discount")
    public ResponseEntity<List<BookDto>> getMaxDiscountBooks() {
        return ResponseEntity.ok(apiService.getBooksWithMaxDiscount());
    }

    @GetMapping("/books/bestsellers")
    @ApiOperation("get bestsellers")
    public ResponseEntity<List<BookDto>> getBestsellers() {
        return ResponseEntity.ok(apiService.getBestsellers());
    }

    @ResponseBody
    @PostMapping("/bookReview")
    @ApiOperation("post book review")
    public ApiResponse postBookReview(
            @RequestBody() String body) throws Exception {
        return apiService.postReview(body);
    }
}
