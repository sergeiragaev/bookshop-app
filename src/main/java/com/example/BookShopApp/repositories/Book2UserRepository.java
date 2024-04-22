package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {
    @Query(value =
            "SELECT * FROM book2user bu JOIN book2user_type t ON bu.type_id = t.id " +
            "WHERE t.code = :code AND bu.user_id = :userId AND bu.book_id = :bookId LIMIT 1", nativeQuery = true)
    Optional<Book2UserEntity> findBook2UserEntityByUserIdAndBookId(Integer userId, Integer bookId, String code);
}
