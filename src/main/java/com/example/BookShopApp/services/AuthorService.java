package com.example.BookShopApp.services;

import com.example.BookShopApp.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.BookShopApp.model.author.AuthorEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Map<String, List<AuthorEntity>> getAuthorsMap() {
        List<AuthorEntity> authorEntities = authorRepository.findAll();
        return authorEntities.stream().collect(Collectors.groupingBy((AuthorEntity a) -> a.getName().substring(0,1)));
    }
    public AuthorEntity getAuthorBySlug(String slug) {
        return authorRepository.getAuthorEntityBySlug(slug);
    }
}
