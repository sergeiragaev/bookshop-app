package com.example.BookShopApp.model.genre;

import javax.persistence.*;

import com.example.BookShopApp.model.book.BookEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "genre")
public class GenreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", columnDefinition = "INT")
    private GenreEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenreEntity> genres = new ArrayList<>();

    @ManyToMany(mappedBy = "genres")
    private List<BookEntity> books = new ArrayList<>();
}
