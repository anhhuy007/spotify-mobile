package com.example.spotifyclone.features.settings.model;

import com.google.gson.annotations.SerializedName;

public class Language {
    @SerializedName("theme")
    private String theme;

    public Language() {
    }
    public Language(String theme) {
        this.theme = theme;
    }

    // Getters and Setters
    public String getTheme() {
        return theme;
    }

    public void setTheme(String username) {
        this.theme = theme;
    }

}
