package com.example.BookShopApp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchWordDto {
    private String example;

    public SearchWordDto(String example) {
        this.example = example;
    }

    public SearchWordDto() {
    }
}
