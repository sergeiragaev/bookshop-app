package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.book.review.BookReviewEntity;
import com.example.BookShopApp.model.book.review.BookReviewLikeEntity;
import com.example.BookShopApp.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {
    boolean existsByUserAndReview(UserEntity user, BookReviewEntity review);
}
