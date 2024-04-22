package com.example.BookShopApp.security;

import com.example.BookShopApp.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookstoreUserRegister {

    private final BookstoreUserRepository bookstoreUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;

    public BookstoreUser registerNewUser(RegistrationForm registrationForm){

        BookstoreUser userByEmail = bookstoreUserRepository.findBookstoreUserByEmail(registrationForm.getEmail());
        BookstoreUser userByPhone = bookstoreUserRepository.findBookstoreUserByPhone(registrationForm.getPhone());

        if (userByEmail == null && userByPhone == null) {
            BookstoreUser user = new BookstoreUser();
            user.setEmail(registrationForm.getEmail());
            user.setName(registrationForm.getName());
            user.setPhone(registrationForm.getPhone());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            bookstoreUserRepository.save(user);
            return user;
        } else {
            return userByPhone;
        }
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }
    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload) {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPhone(payload.getContact());
        registrationForm.setPass(payload.getCode());
        registerNewUser(registrationForm);
        UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public Object getCurrentUser() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof DefaultOAuth2User){
            return createReturnUserFromOAuthPrincipal(userDetails);
        } else {
            BookstoreUserDetails bookstoreUserDetails =
                    (BookstoreUserDetails) userDetails;
            return bookstoreUserDetails.getBookstoreUser();
        }
    }

    private Object createReturnUserFromOAuthPrincipal(Object userDetails) {
        Map<String, Object> mp = ((OAuth2AuthenticatedPrincipal) userDetails).getAttributes();
        String username= (String) mp.get("name");
        String email = (String) mp.get("email");
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByEmail(email);
        if (bookstoreUser == null) {
            bookstoreUser = new BookstoreUser();
            bookstoreUser.setEmail(email);
            bookstoreUser.setName(username);
            bookstoreUserRepository.save(bookstoreUser);
        }
        bookstoreUser.setName(username);
        return bookstoreUser;
    }

}
