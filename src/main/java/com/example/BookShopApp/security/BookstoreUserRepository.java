package com.example.BookShopApp.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookstoreUserRepository extends JpaRepository<BookstoreUser, Integer> {

    BookstoreUser findBookstoreUserByEmail(String email);
    BookstoreUser findBookstoreUserByName(String name);
    BookstoreUser findBookstoreUserByPhone(String phone);
}
