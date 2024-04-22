package com.example.BookShopApp.model.book.review;

import javax.persistence.*;

import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.model.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "book_review")
public class BookReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BookEntity book;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @JsonIgnore
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "book_review_like",
//            joinColumns = @JoinColumn(name = "review_id"),
//            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<BookReviewLikeEntity> reviewLikes;

}
