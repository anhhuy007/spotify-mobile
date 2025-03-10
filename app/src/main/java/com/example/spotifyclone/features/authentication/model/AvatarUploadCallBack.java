package com.example.spotifyclone.features.authentication.model;

public interface AvatarUploadCallBack {
    void onSuccess(String avatarUrl);
    void onFailure(String error);
}
