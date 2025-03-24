package com.example.spotifyclone.features.profile.model;

public class Profile {
    private String id;
    private String name;
    private String password;
    private int follow;
    private int follower;
    private String avatarUrl;
    private String createdAt;

    public Profile(String id, String name, String avatarUrl, String password) {
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

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setFollower(int follower) { this.follower = follower; }
    public void setFollow(int follow) { this.follow = follow; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
