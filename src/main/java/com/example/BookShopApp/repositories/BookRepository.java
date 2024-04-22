package com.example.BookShopApp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BookShopApp.model.book.BookEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    List<BookEntity> findBookEntitiesByTitleContaining(String bookTitle);
    List<BookEntity> findBookEntitiesByPriceBetween(Integer min, Integer max);

    List<BookEntity> findBookEntitiesByPriceIs(Integer price);


    @Query("from BookEntity where isBestseller = 1")
    List<BookEntity> getBestsellers();

    @Query(value = "SELECT * FROM book where discount = (Select Max(discount) from book)", nativeQuery = true)
    List<BookEntity> getBookEntitiesWithMaxDiscount();

    Page<BookEntity> findBookEntitiesByTitleContaining(String title, Pageable nextPage);

    Page<BookEntity> findBookEntitiesByPubDateBetweenOrderByPubDateDesc(Date start, Date end, Pageable nextPage);

    @Query(value =
            "SELECT * FROM book b JOIN ( " +
                    "SELECT book_id, MAX(bought) as bought, MAX(incart) as incart, MAX(postponed) as postponed FROM ( " +
                    "SELECT bu.book_id, COUNT(bu.*) as bought, 0 as incart, 0 as postponed " +
                    "FROM book2user bu LEFT JOIN book2user_type t ON bu.type_id = t.id WHERE t.code NOT IN ('KEPT', 'CART') " +
                    "GROUP BY bu.book_id " +
                    "UNION ALL " +
                    "SELECT bu.book_id, 0 as bought, COUNT(bu.*) as incart, 0 as postponed " +
                    "FROM book2user bu LEFT JOIN book2user_type t ON bu.type_id = t.id WHERE t.code = 'CART' " +
                    "GROUP BY bu.book_id " +
                    "UNION ALL " +
                    "SELECT bu.book_id, 0 as bought, 0 as incart, COUNT(bu.*) as postponed " +
                    "FROM book2user bu LEFT JOIN book2user_type t ON bu.type_id = t.id WHERE t.code = 'KEPT' " +
                    "GROUP BY bu.book_id) AS p GROUP BY p.book_id) AS p ON b.id = p.book_id " +
                    "ORDER BY ( p.bought + p.incart * 0.7 + p.postponed * 0.4) DESC", nativeQuery = true)
    Page<BookEntity> getPopularBookEntities(Pageable nextPage);

    @Query(value = "SELECT * FROM book WHERE id IN (" +
            "SELECT bt.book_id FROM book2tag bt JOIN tag t ON bt.tag_id=t.id WHERE t.slug = :slug)", nativeQuery = true)
    Page<BookEntity> findBookEntitiesByTag(String slug, Pageable nextPage);

    @Query(value = "SELECT * FROM book WHERE id IN (" +
            "SELECT bg.book_id FROM book2genre bg JOIN genre g ON bg.genre_id=g.id WHERE g.slug = :slug)", nativeQuery = true)
    Page<BookEntity> findBookEntitiesByGenre(String slug, Pageable nextPage);

    @Query(value = "SELECT * FROM book WHERE id IN (" +
            "SELECT ba.book_id FROM book2author ba JOIN author a ON ba.author_id=a.id WHERE a.slug = :slug)", nativeQuery = true)
    Page<BookEntity> findBookEntitiesByAuthor(String slug, Pageable nextPage);

    BookEntity findBookEntityBySlug(String slug);

    List<BookEntity> findBookEntitiesBySlugIn(Collection<String> slug);

    @Query(value = "SELECT AVG(value) FROM rating r JOIN book b ON r.book_id = b.id WHERE b.slug = :slug GROUP BY r.book_id", nativeQuery = true)
    Optional<Double> getAverageRating(String slug);

    @Query(value = "SELECT * FROM book WHERE id IN (" +
            "SELECT bu.book_id FROM book2user bu JOIN book2user_type t ON bu.type_id = t.id " +
            "WHERE t.code = :code AND bu.user_id = :userId)", nativeQuery = true)
    List<BookEntity> findBookEntitiesByUserAndType(Integer userId, String code);

    @Query(value = "SELECT * FROM book WHERE id IN (" +
            "SELECT bu.book_id FROM book2user bu " +
            "WHERE bu.user_id = :userId)", nativeQuery = true)
    List<BookEntity> findBookEntitiesByUser(Integer userId);

    @Query(value = "SELECT COUNT(*) FROM book WHERE id IN (" +
            "SELECT bu.book_id FROM book2user bu JOIN book2user_type t ON bu.type_id = t.id " +
            "WHERE t.code NOT IN ('KEPT', 'CART') AND bu.user_id = :userId)", nativeQuery = true)
    Integer countBookEntitiesByUserId(Integer userId);

    @Query(value = "SELECT * FROM book b JOIN (" +
            "SELECT AVG(value) AS avg, COUNT(value) as cnt, book_id " +
            "FROM rating r GROUP BY r.book_id" +
                    ") AS r ON b.id = r.book_id " +
                    "ORDER BY r.avg DESC, r.cnt DESC, b.pub_date DESC", nativeQuery = true)
    Page<BookEntity> getRecommendedBookEntities(Pageable nextPage);

    @Query(value =
            "SELECT * FROM book b JOIN (" +
                    "SELECT DISTINCT avg, cnt, book_id FROM (" +
                    "SELECT AVG(value) AS avg, COUNT(value) as cnt, book_id " +
                    "FROM rating r WHERE r.book_id IN (SELECT bg.book_id FROM book2genre bg " +
                    "WHERE bg.genre_id IN (SELECT bg.genre_id FROM book2genre bg WHERE bg.book_id IN :bookIdList)) " +
                    "GROUP BY r.book_id " +
                    "UNION ALL " +
                    "SELECT AVG(value) AS avg, COUNT(value) as cnt, book_id " +
                    "FROM rating r WHERE r.book_id IN (SELECT bt.book_id FROM book2tag bt " +
                    "WHERE bt.tag_id IN (SELECT bt.tag_id FROM book2tag bt WHERE bt.book_id IN :bookIdList)) " +
                    "GROUP BY r.book_id " +
                    "UNION ALL " +
                    "SELECT AVG(value) AS avg, COUNT(value) as cnt, book_id " +
                    "FROM rating r WHERE r.book_id IN (SELECT ba.book_id FROM book2author ba " +
                    "WHERE ba.author_id IN (SELECT ba.author_id FROM book2author ba WHERE ba.book_id IN :bookIdList)) " +
                    "GROUP BY r.book_id " +
                    ") AS r ) as r ON b.id = r.book_id " +
                    "ORDER BY r.avg DESC, r.cnt DESC, b.pub_date DESC", nativeQuery = true)
    Page<BookEntity> getRecommendedBookEntities(Pageable nextPage, Collection<Integer> bookIdList);
    @Query(value = "SELECT COUNT(*) FROM book2user bu JOIN book2user_type t ON bu.type_id = t.id " +
            "WHERE t.code NOT IN ('KEPT', 'CART') AND bu.book_id = :bookId", nativeQuery = true)
    Integer countOfBoughtBooks(Integer bookId);
    @Query(value = "SELECT COUNT(*) FROM book2user bu JOIN book2user_type t ON bu.type_id = t.id " +
            "WHERE t.code = 'CART' AND bu.book_id = :bookId", nativeQuery = true)
    Integer countOfBooksInCart(Integer bookId);
    @Query(value = "SELECT COUNT(*) FROM book2user bu JOIN book2user_type t ON bu.type_id = t.id " +
            "WHERE t.code = 'KEPT' AND bu.book_id = :bookId", nativeQuery = true)
    Integer countOfPostponedBooks(Integer bookId);
}
