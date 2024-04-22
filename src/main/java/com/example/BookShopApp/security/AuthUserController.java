package com.example.BookShopApp.security;

import com.example.BookShopApp.annotations.WarningExceptionsLoggable;
import com.example.BookShopApp.errs.UserAlreadyExistsException;
import com.example.BookShopApp.model.SmsCode;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.repositories.BookRepository;
import com.example.BookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class AuthUserController {

    private final BookstoreUserRegister userRegister;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final SmsService smsService;
    private final JavaMailSender javaMailSender;

    @GetMapping("/signin")
    public String handleSignIn(){
        return "/signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model){
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleContactConfirmation(@RequestBody ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        if (!payload.getContact().contains("@")) {
            String smsCodeString = smsService.sendSecretCodeSms(payload.getContact());
            smsService.saveNewCode(new SmsCode(smsCodeString, 60)); //expires in 1 min.
        }
        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("test@mir-kuhni.ru");
        message.setTo(payload.getContact());
        SmsCode smsCode = new SmsCode(smsService.generateCode(), 300);
        smsService.saveNewCode(smsCode);
        message.setSubject("Bookstore email verification!");
        message.setText("Verification code is: " + smsCode.getCode());
        javaMailSender.send(message);
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();

        if (smsService.verifyCode(payload.getCode())) {
            response.setResult("true");
        }
        return response;
    }
    @PostMapping("/reg")
    @WarningExceptionsLoggable
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) throws Exception {
        if (userRegister.registerNewUser(registrationForm) != null) {
            model.addAttribute("regOk", true);
            return "signin";
        } else {
            throw new UserAlreadyExistsException("User with such credentials already exists");
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse response){
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        response.addCookie(cookie);
        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse response){
        if (smsService.verifyCode(payload.getCode())) {
            ContactConfirmationResponse loginResponse = userRegister.jwtLoginByPhoneNumber(payload);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            response.addCookie(cookie);
            return loginResponse;
        } else {
            return null;
        }
    }

    @GetMapping("/my")
    public String handleMy(Model model){
        List<BookDto> myBooks = bookService.fillBookDtoList(
                bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "PAID"));
        model.addAttribute("books", myBooks);
        return "my";
    }
    @GetMapping("/profile")
    public String handleProfile(){
        return "profile";
    }

    @GetMapping("/myarchive")
    public String handleMyArchive(Model model){
        List<BookDto> myBooks = bookService.fillBookDtoList(
                bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "ARCHIVED"));
        model.addAttribute("books", myBooks);
        return "myarchive";
    }
}
