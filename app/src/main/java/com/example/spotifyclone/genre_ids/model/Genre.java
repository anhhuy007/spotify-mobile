package com.example.spotifyclone.genre_ids.model;


import java.util.Date;

public class Genre {
    private String _id;
    private String name;
    private String description;
    private String image_url;
    private Date create_at;
    private Date updated_at;

    public Genre(String id, String name, String description, String image_url, Date create_at, Date updated_at) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.create_at = create_at;
        this.updated_at = updated_at;
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

    public void setImg_url(String img_url) {
        this.image_url = img_url;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
