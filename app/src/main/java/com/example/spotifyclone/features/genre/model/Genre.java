package com.example.spotifyclone.features.genre.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Genre implements Parcelable {
    @SerializedName("_id")
    private String _id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("create_at")
    private Date create_at;

    @SerializedName("update_at")
    private Date update_at;

    // Constructor mặc định
    public Genre() {
    }

    // Constructor cho Parcelable
    protected Genre(Parcel in) {
        _id = in.readString();
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
        create_at = new Date(in.readLong());
        update_at = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image_url);
        dest.writeLong(create_at != null ? create_at.getTime() : -1);
        dest.writeLong(update_at != null ? update_at.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    // Getter và Setter
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
