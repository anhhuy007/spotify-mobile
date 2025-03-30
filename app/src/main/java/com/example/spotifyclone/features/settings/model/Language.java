package com.example.spotifyclone.features.settings.model;

import com.google.gson.annotations.SerializedName;

public class Language {
    @SerializedName("language")
    private String language;
    public Language() {
    }
    public Language(String language) {
        this.language = language;
    }

    // Getters and Setters
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String username) {
        this.language = language;
    }

}
