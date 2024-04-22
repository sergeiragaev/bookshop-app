package com.example.BookShopApp.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContactConfirmationPayload {
    private String contact;
    private String code;
}
