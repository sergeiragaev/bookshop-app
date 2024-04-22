package com.example.BookShopApp.controllers;

import com.example.BookShopApp.data.ApiResponse;
import com.example.BookShopApp.errs.*;
import com.example.BookShopApp.model.dto.BookDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchException.class)
    public String handleEmptySearchException(EmptySearchException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchError", e);
        return "redirect:/";
    }
    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse> handleBookstoreApiWrongParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad parameter value...",
                exception), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("userRegisterError", e);
        return "redirect:/signup";
    }
    @ExceptionHandler(BookstoreJwtException.class)
    public ResponseEntity<ApiResponse> handleBookstoreJwtException(BookstoreJwtException e){
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.UNAUTHORIZED, e.getLocalizedMessage(), e), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UserAlreadyRateReviewException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyRateReviewException(Exception e){
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.FORBIDDEN, e.getLocalizedMessage(), e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserCannotRateOwnReviewException.class)
    public ResponseEntity<ApiResponse> handleUserCannotRateOwnReviewException(Exception e){
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.FORBIDDEN, e.getLocalizedMessage(), e), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(BookStoreApiShortReviewException.class)
    public ResponseEntity<ApiResponse> handleBookStoreApiShortReviewException(Exception e){
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.FORBIDDEN, e.getLocalizedMessage(), e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserReviewAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserReviewAlreadyExistsException(Exception e){
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.FORBIDDEN, e.getLocalizedMessage(), e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<BookDto>> handleMissingServletRequestParameterException(Exception exception) {
        Logger.getLogger(this.getClass().getSimpleName()).warning(exception.getLocalizedMessage());
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing required parameters",
                exception), HttpStatus.BAD_REQUEST);
    }
}
