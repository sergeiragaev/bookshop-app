package com.example.BookShopApp.errs;

public class EmptySearchException extends Exception {
    public EmptySearchException(String message) {
        super(message);
    }
}
