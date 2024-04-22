package com.example.BookShopApp.errs;

public class UserReviewAlreadyExistsException extends Exception {
    public UserReviewAlreadyExistsException(String message) {
        super(message);
    }
}