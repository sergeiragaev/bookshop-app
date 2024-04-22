package com.example.BookShopApp.services;

import com.example.BookShopApp.model.tag.TagEntity;
import com.example.BookShopApp.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    public List<TagEntity> getTagData() {
        return tagRepository.findAll();
    }

    public TagEntity getTagBySlug(String slug) {
        return tagRepository.getTagEntityBySlug(slug);
    }
}
