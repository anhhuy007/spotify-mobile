package com.example.spotifyclone.features.profile.model;

import com.google.gson.annotations.SerializedName;

public class ProfileUpdate {
    @SerializedName("username")
    private String username;

    @SerializedName("avatar_url")
    private String avatarUrl;

    public ProfileUpdate(String username, String avatarUrl) {
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}