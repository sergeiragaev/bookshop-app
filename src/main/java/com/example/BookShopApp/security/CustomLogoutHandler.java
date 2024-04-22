package com.example.BookShopApp.security;

import com.example.BookShopApp.security.jwt.JWTBlackListEntity;
import com.example.BookShopApp.security.jwt.JWTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JWTRepository jwtRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    JWTBlackListEntity jwtBlackListEntity = new JWTBlackListEntity();
                    jwtBlackListEntity.setToken(token);
                    jwtRepository.save(jwtBlackListEntity);
                }
            }
        }
    }
}