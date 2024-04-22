package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {
    GenreEntity getGenreEntityBySlug(String slug);
}
