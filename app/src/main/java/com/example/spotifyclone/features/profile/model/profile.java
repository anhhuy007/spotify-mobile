package com.example.spotifyclone.features.profile.model;

public class profile {
    private String id;
    private String name;
    private String password;
    private int follow;
    private int follower;
    private String avatarUrl;
    private String createdAt;

    public profile(String id, String name, String avatarUrl, String password) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public int getFollower() { return follower; }
    public int getFollow() { return follow; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCreatedAt() { return createdAt; }
}
