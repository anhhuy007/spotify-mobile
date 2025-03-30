package com.example.spotifyclone.features.artist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {
    private String _id;
    private String name;
    private String bio;
    private String avatar_url;
    private int followers;

    public Artist(String _id, String name, String bio, String avatar_url, int followers) {
        this._id = _id;
        this.name = name;
        this.bio = bio;
        this.avatar_url = avatar_url;
        this.followers = followers;
    }

    protected Artist(Parcel in) {
        _id = in.readString();
        name = in.readString();
        bio = in.readString();
        avatar_url = in.readString();
        followers = in.readInt();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public String getId() { return _id; }
    public String getName() { return name; }
    public String getDescription() { return bio; }
    public String getAvatarUrl() { return avatar_url; }
    public int getFollowers() { return followers; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(bio);
        dest.writeString(avatar_url);
        dest.writeInt(followers);
    }
}
