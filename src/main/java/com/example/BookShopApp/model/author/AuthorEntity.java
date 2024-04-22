package com.example.BookShopApp.model.author;

import com.example.BookShopApp.model.book.BookEntity;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "author")
@ApiModel(description = "entity representing an author")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String photo;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "authors")
    private List<BookEntity> books = new ArrayList<>();

    public AuthorEntity(List<String> authors){
        if (authors != null){
            this.setName(authors.toString());
        }
    }
    public AuthorEntity(){}

}
