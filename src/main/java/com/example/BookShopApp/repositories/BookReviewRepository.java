package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {
}
