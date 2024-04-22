package com.example.BookShopApp.errs;

public class UserAlreadyRateReviewException extends Exception {
    public UserAlreadyRateReviewException(String message) {
        super(message);
    }
}
