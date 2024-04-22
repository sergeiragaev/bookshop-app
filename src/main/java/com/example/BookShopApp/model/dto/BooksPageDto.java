package com.example.BookShopApp.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BooksPageDto {

    private int count;
    private List<BookDto> books;

    public BooksPageDto(List<BookDto> books) {
        this.books = books;
        this.count = books.size();
    }
    public BooksPageDto(List<BookDto> books, int count) {
        this.books = books;
        this.count = count;
    }
}
