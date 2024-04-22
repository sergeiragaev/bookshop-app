package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.book.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Integer> {
}
