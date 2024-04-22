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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final Book2UserRepository book2UserRepository;

    public void setCartContents(String cartContents, Model model) {
        try{
            List<BookEntity> cartBooks =
                    bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "CART");
            cartContents = cartBooks.stream().map(BookEntity::getSlug).collect(Collectors.joining("/"));
        } catch (Exception e){
        }
        if (cartContents == null || cartContents.equals("")){
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
            cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length()-1) : cartContents;
            Collection<String> cookieSlugs = Arrays.stream(cartContents.split("/")).toList();
            List<BookEntity> booksFromCookiesSlugs = bookRepository.findBookEntitiesBySlugIn(cookieSlugs);
            List<BookDto> bookDtoList = new ArrayList<>();
            for (BookEntity book : booksFromCookiesSlugs ){
                Optional<Double> optionalRating = bookRepository.getAverageRating(book.getSlug());
                bookDtoList.add(
                        new BookDto(book,
                                (int) (optionalRating.isPresent() ? Math.round(optionalRating.get()) : 0),
                                "CART"));
            }
            model.addAttribute("bookCart", bookDtoList);
            if (bookDtoList.size() > 0) {
                model.addAttribute("totalPriceOld",
                        bookDtoList.stream().map(BookDto::price).reduce(Integer::sum).get());
                model.addAttribute("totalPrice",
                        bookDtoList.stream().map(BookDto::discountPrice).reduce(Integer::sum).get());
            }
        }
    }

    public void addToCartContents(String slug, String cartContents, HttpServletResponse response, Model model) {
        try{
            BookstoreUser user = (BookstoreUser) userRegister.getCurrentUser();
            List<BookEntity> cartBooks =
                    bookRepository.findBookEntitiesByUserAndType(user.getId(), "CART");
            cartContents = cartBooks.stream().map(BookEntity::getSlug).collect(Collectors.joining("/"));
            if (!cartContents.contains(slug)){
                BookEntity book = bookRepository.findBookEntityBySlug(slug);
                Book2UserEntity book2User = new  Book2UserEntity();
                book2User.setId(book2UserRepository.findAll().size() + 1);
                book2User.setUserId(user.getId());
                book2User.setBookId(book.getId());
                book2User.setTypeId(2);
                book2User.setTime(LocalDateTime.now());
                book2UserRepository.save(book2User);
            }
        } catch (Exception e){
            if (cartContents == null || cartContents.equals("")){
                Cookie cookie = new Cookie("cartContents", slug);
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            } else if (!cartContents.contains(slug)){
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(cartContents).add(slug);
                Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            }
        }
    }

    public void removeFromCartContents(String slug, String cartContents, HttpServletResponse response, Model model) {
        try{
            BookstoreUser user = (BookstoreUser) userRegister.getCurrentUser();
            List<BookEntity> cartBooks =
                    bookRepository.findBookEntitiesByUserAndType(user.getId(), "CART");
            cartContents = cartBooks.stream().map(BookEntity::getSlug).collect(Collectors.joining("/"));
            if (cartContents.contains(slug)){
                BookEntity book = bookRepository.findBookEntityBySlug(slug);
                Optional<Book2UserEntity> book2UserEntityOptional =
                        book2UserRepository.findBook2UserEntityByUserIdAndBookId(user.getId(), book.getId(), "CART");
                book2UserEntityOptional.ifPresent(book2UserRepository::delete);
                model.addAttribute("isCartEmpty",
                        bookRepository.findBookEntitiesByUserAndType(user.getId(), "CART").isEmpty());
            }
        } catch (Exception e){
            if (cartContents != null && !cartContents.equals("")){
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                cookieBooks.remove(slug);
                Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            } else {
                model.addAttribute("isCartEmpty", true);
            }
        }
    }
}
