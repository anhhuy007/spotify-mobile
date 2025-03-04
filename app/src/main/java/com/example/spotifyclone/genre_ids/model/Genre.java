package com.example.spotifyclone.genre_ids.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Genre {
    @SerializedName("_id")
    private String _id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("create_at")
    private Date create_at; // Đổi tên thành createdAt (camelCase)

    @SerializedName("update_at")
    private Date update_at; // Đổi tên thành updatedAt (camelCase)

    public Genre() {
        // Constructor mặc định là cần thiết để Gson hoạt động đúng cách
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public Date getCreatedAt() {
        return create_at;
    }

    public void setCreatedAt(Date createdAt) {
        this.create_at = createdAt;
    }

    public Date getUpdatedAt() {
        return update_at;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.update_at = updatedAt;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + image_url + '\'' +
                ", createdAt=" + create_at +
                ", updatedAt=" + update_at +
                '}';
    }
}