package com.example.BookShopApp.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collection;

@Setter
@Getter
public class ApiResponse<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timeStamp;
    private String error;
    private String debugMessage;
    private Collection<T> data;
    private int status;
    private boolean result;

    public ApiResponse(){
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponse(HttpStatus httpStatus, String error, Throwable ex) {
        this();
        this.error = error;
        this.debugMessage = ex.getLocalizedMessage();
        this.status = httpStatus.value();
    }
}
