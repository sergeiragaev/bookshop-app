package com.example.BookShopApp.data;

import com.example.BookShopApp.model.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {
    SmsCode findByCode(String code);
}
