package com.example.BookShopApp.errs;

import io.jsonwebtoken.JwtException;

public class BookstoreJwtException extends JwtException {
    public BookstoreJwtException(String message) {
        super(message);
    }
}