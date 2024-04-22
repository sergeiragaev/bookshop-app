package com.example.BookShopApp.model.dto;

import com.example.BookShopApp.model.book.review.BookReviewEntity;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Optional;


@Getter
public class ReviewDto implements Comparable<ReviewDto> {

    private final int id;
    private final String time;
    private final String text;
    private final String extendedText;
    private int likes;
    private int dislikes;
    private final String userName;
    private double rating;

    public ReviewDto(BookReviewEntity bookReview) {
        this.id = bookReview.getId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        this.time = dateFormat.format(bookReview.getTime().toEpochSecond(ZoneOffset.of("+3"))*1000);
        this.text = bookReview.getText();
        this.extendedText = this.text;
        this.userName = bookReview.getUser().getName();
        if (bookReview.getReviewLikes() != null) {
            Optional<Short> optionalLikes = bookReview.getReviewLikes().stream().map(l -> l.getValue()).filter(value -> value > 0).reduce((l1, l2) -> (short) (l1 + l2));
            this.likes = optionalLikes.isPresent() ? optionalLikes.get() : 0;
            Optional<Short> optionalDislikes = bookReview.getReviewLikes().stream().map(l -> l.getValue()).filter(value -> value < 0).reduce((l1, l2) -> (short) (l1 + l2));
            this.dislikes = optionalDislikes.isPresent() ? optionalDislikes.get() * -1 : 0;
            if (this.likes != 0 || this.dislikes != 0) {
                this.rating = (double) (this.likes * 5) / (this.likes + this.dislikes);
            }
        }
    }

    @Override
    public int compareTo(ReviewDto o) {
        return (int) ((o.getRating() - this.rating) * 100000000 + o.getId() - this.getId());
    }
}
