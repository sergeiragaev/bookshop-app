package com.example.BookShopApp.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
public interface JWTRepository extends JpaRepository<JWTBlackListEntity, Integer> {
    JWTBlackListEntity findJWTBlackListEntityByToken(String token);
}
