package com.example.BookShopApp.errs;

public class UserCannotRateOwnReviewException extends Exception {
    public UserCannotRateOwnReviewException(String message) {
        super(message);
    }
}
