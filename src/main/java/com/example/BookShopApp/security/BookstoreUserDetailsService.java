package com.example.BookShopApp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookstoreUserDetailsService implements UserDetailsService {

    private final BookstoreUserRepository bookstoreUserRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(s);
        if (bookstoreUser == null){
            bookstoreUser = bookstoreUserRepository.findBookstoreUserByName(s);
            if (bookstoreUser == null){
                bookstoreUser = bookstoreUserRepository.findBookstoreUserByPhone(s);
            }
        }
        if (bookstoreUser != null){
            return new BookstoreUserDetails(bookstoreUser);
        } else {
            throw new UsernameNotFoundException("user not found doh!");
        }
    }
}
