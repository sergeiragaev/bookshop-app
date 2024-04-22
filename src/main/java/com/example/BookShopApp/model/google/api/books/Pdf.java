package com.example.BookShopApp.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pdf {
    @JsonProperty("isAvailable")
    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    boolean isAvailable;

    @JsonProperty("downloadLink")
    public String getDownloadLink() {
        return this.downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    String downloadLink;
}
