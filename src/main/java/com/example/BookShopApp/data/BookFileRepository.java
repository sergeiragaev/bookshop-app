package com.example.BookShopApp.data;

import com.example.BookShopApp.model.book.file.BookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {

    BookFileEntity findBookFileEntityByHash(String hash);
}
