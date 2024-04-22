package com.example.BookShopApp.services;

import com.example.BookShopApp.model.genre.GenreEntity;
import com.example.BookShopApp.repositories.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    public List<GenreEntity> getGenreData() {
        return genreRepository.findAll();
    }

    public GenreEntity getGenreBySlug(String slug) {
        return genreRepository.getGenreEntityBySlug(slug);
    }
}
