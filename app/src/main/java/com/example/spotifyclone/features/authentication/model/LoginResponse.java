package com.example.spotifyclone.features.authentication.model;
import com.example.spotifyclone.shared.model.User;

public class LoginResponse {
    private User user;
    private Tokens tokens;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tokens getTokens() {
        return tokens;
    }

    public void setTokens(Tokens tokens) {
        this.tokens = tokens;
    }
}

