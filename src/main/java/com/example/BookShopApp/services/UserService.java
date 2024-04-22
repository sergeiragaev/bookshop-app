package com.example.BookShopApp.services;

import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.security.BookstoreUserRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BookstoreUserRegister userRegister;

    public BookstoreUser getCurUsr() {
        try {
            return (BookstoreUser) userRegister.getCurrentUser();
        } catch (Exception e){
            return new BookstoreUser();
        }
    }
    public String getStatus() {
        try {
            return (userRegister.getCurrentUser() == null) ? "unauthorized" : "authorized";
        } catch (Exception e){
            return "unauthorized";
        }
    }
}
