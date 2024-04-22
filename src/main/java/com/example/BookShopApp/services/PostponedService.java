package com.example.BookShopApp.services;

import com.example.BookShopApp.model.book.BookEntity;
import com.example.BookShopApp.model.book.links.Book2UserEntity;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.repositories.Book2UserRepository;
import com.example.BookShopApp.repositories.BookRepository;
import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.security.BookstoreUserRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostponedService {

    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final Book2UserRepository book2UserRepository;

    public void setPostponedContents(String postponedContents, Model model) {
        try{
            List<BookEntity> postponedBooks = bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "KEPT");

            postponedContents = postponedBooks.stream().map(BookEntity::getSlug).collect(Collectors.joining("/"));
        } catch (Exception e){
        }
        if (postponedContents == null || postponedContents.equals("")){
            model.addAttribute("isPostponedEmpty", true);
        } else {
            model.addAttribute("isPostponedEmpty", false);
            postponedContents = postponedContents.startsWith("/") ? postponedContents.substring(1) : postponedContents;
            postponedContents = postponedContents.endsWith("/") ? postponedContents.substring(0, postponedContents.length()-1) : postponedContents;
            Collection<String> cookieSlugs = Arrays.stream(postponedContents.split("/")).toList();
            List<BookEntity> booksFromCookiesSlugs = bookRepository.findBookEntitiesBySlugIn(cookieSlugs);
            List<BookDto> bookDtoList = new ArrayList<>();
            for (BookEntity book : booksFromCookiesSlugs ){
                Optional<Double> optionalRating = bookRepository.getAverageRating(book.getSlug());
                bookDtoList.add(
                        new BookDto(book,
                                (int) (optionalRating.isPresent() ? Math.round(optionalRating.get()) : 0),
                        "KEPT"));
            }
            model.addAttribute("bookPostponed", bookDtoList);
        }
    }

    public void addToPostponedContents(String slug, String postponedContents, HttpServletResponse response, Model model) {
        if (postponedContents == null || postponedContents.equals("")){
            Cookie cookie = new Cookie("postponedContents", slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isPostponedEmpty", false);
        } else if (!postponedContents.contains(slug)){
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(postponedContents).add(slug);
            Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isPostponedEmpty", false);
        }
    }

    public void removeFromPostponedContents(String slug, String postponedContents, HttpServletResponse response, Model model) {
        try{
            BookstoreUser user = (BookstoreUser) userRegister.getCurrentUser();
            List<BookEntity> keptBooks =
                    bookRepository.findBookEntitiesByUserAndType(user.getId(), "KEPT");
            postponedContents = keptBooks.stream().map(BookEntity::getSlug).collect(Collectors.joining("/"));
            if (postponedContents.contains(slug)){
                BookEntity book = bookRepository.findBookEntityBySlug(slug);
                Optional<Book2UserEntity> book2UserEntityOptional =
                        book2UserRepository.findBook2UserEntityByUserIdAndBookId(user.getId(), book.getId(), "KEPT");
                book2UserEntityOptional.ifPresent(book2UserRepository::delete);
                model.addAttribute("isPostponedEmpty",
                        bookRepository.findBookEntitiesByUserAndType(user.getId(), "KEPT").isEmpty());
            }
        } catch (Exception e){
            if (postponedContents != null && !postponedContents.equals("")){
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                cookieBooks.remove(slug);
                Cookie cookie = new Cookie("postponedContents", String.join("/", cookieBooks));
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            } else {
                model.addAttribute("isPostponedEmpty", true);
            }
        }
    }
}
