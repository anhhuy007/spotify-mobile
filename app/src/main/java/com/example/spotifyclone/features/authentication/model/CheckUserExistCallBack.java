package com.example.spotifyclone.features.authentication.model;

public interface CheckUserExistCallBack {
    void onUserExist(Boolean isExisted);
    void onFailure(String error);
}
