package com.example.BookShopApp.model.book;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "rating")
public class RatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Integer value;

    @Column(name="book_id", columnDefinition = "INT NOT NULL")
    private Integer bookId;
}
