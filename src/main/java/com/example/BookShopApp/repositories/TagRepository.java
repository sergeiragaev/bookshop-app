package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.tag.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Integer> {
    TagEntity getTagEntityBySlug(String slug);
}
