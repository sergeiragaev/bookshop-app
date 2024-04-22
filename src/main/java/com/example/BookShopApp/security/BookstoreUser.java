package com.example.BookShopApp.security;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name ="`user`")
@Getter
@Setter
public class BookstoreUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;
}
