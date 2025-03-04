package com.example.spotifyclone.shared.model;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        @SerializedName("accessToken")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

