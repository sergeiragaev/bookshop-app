package com.example.BookShopApp.model.dto;

import com.example.BookShopApp.model.author.AuthorEntity;
import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.model.book.RatingEntity;
import com.example.BookShopApp.model.book.file.BookFileEntity;
import com.example.BookShopApp.model.book.review.BookReviewEntity;
import com.example.BookShopApp.model.genre.GenreEntity;
import com.example.BookShopApp.model.tag.TagEntity;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BookDto {

    @JsonIgnore
    private final BookEntity book;
    @JsonProperty
    public int id() {
        return book.getId();
    }
    @JsonProperty
    public String slug() {
        return book.getSlug();
    }
    @JsonProperty
    public String image() {
        return book.getImage();
    }
    @JsonGetter("getAuthors")
    public List<AuthorEntity> getAuthors() {
        return book.getAuthors();
    }
    @JsonProperty
    public String title() {
        return book.getTitle();
    }
    @JsonProperty
    public int discount() {
        return Math.toIntExact(Math.round(book.getDiscount()*100));
    }
    @JsonProperty
    public int isBestseller() {
        return book.getIsBestseller();
    }
    private final int rating;
    private final String status;
    @JsonProperty
    public int price() {
        return book.getPrice();
    }
    @JsonProperty
    public String author(){
        return book.getAuthors().stream().map(AuthorEntity::getName).collect(Collectors.joining("<br>"));
    }
    @JsonProperty
    public int discountPrice(){
        return book.getPrice() - Math.toIntExact(Math.round(book.getDiscount()*book.getPrice()));
    }

    @JsonIgnore
    public List<RatingEntity> ratings(){
        return book.getRatings();
    }
    @JsonIgnore
    public List<TagEntity> getTags(){
        return book.getTags();
    }
    @JsonIgnore
    public List<GenreEntity> genres(){
        return book.getGenres();
    }

    @JsonIgnore
    public List<BookFileEntity> getBookFileList(){
        return book.getBookFileList();
    }
    @JsonIgnore
    public List<BookReviewEntity> getReviews(){
        return book.getReviews();
    }
    @JsonIgnore
    public String description(){
        return book.getDescription();
    }

    public BookDto(BookEntity book, int rating, String status) {
        this.book = book;
        this.rating = rating;
        this.status = status;
    }
}
