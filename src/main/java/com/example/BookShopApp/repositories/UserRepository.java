package com.example.BookShopApp.repositories;

import com.example.BookShopApp.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
