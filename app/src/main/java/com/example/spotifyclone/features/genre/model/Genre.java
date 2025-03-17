package com.example.spotifyclone.features.genre.model;

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Date update_at) {
        this.update_at = update_at;
    }
}