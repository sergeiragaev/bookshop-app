package com.example.BookShopApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BookShopApp.model.author.AuthorEntity;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {
    AuthorEntity getAuthorEntityBySlug(String slug);
}
