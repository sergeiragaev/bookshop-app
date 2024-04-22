package com.example.BookShopApp.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SaleInfo {
    @JsonProperty("country")
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    String country;

    @JsonProperty("saleability")
    public String getSaleability() {
        return this.saleability;
    }

    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    String saleability;

    @JsonProperty("isEbook")
    public boolean getIsEbook() {
        return this.isEbook;
    }

    public void setIsEbook(boolean isEbook) {
        this.isEbook = isEbook;
    }

    boolean isEbook;

    @JsonProperty("buyLink")
    public String getBuyLink() {
        return this.buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    String buyLink;
}
