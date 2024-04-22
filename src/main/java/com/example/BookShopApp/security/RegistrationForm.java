package com.example.BookShopApp.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationForm {
    private String name;
    private String email;
    private String phone;
    private String pass;
}
