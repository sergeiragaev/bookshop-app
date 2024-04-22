package com.example.BookShopApp.model.book.links;

import javax.persistence.*;

@Entity
@Table(name = "book2tag")
public class Book2TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private int bookId;

    @Column(name = "tag_id", columnDefinition = "INT NOT NULL")
    private int tagId;
}
