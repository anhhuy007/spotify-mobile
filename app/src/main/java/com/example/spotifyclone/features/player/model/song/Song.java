package com.example.spotifyclone.features.player.model.song;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.genre.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Song implements Parcelable {
    private String _id;
    private String title;
    private String lyric;
    private boolean is_premium;
    private int like_count;
    private String mp3_url;
    private String image_url;
    private List<Artist> singers;
    private List<Artist> authors;
    private List<Genre> genres;

    public Song(Song song) {
        this._id = song._id;
        this.title = song.title;
        this.lyric = song.lyric;
        this.is_premium = song.is_premium;
        this.like_count = song.like_count;
        this.mp3_url = song.mp3_url;
        this.image_url = song.image_url;
        this.singers = new ArrayList<>(song.singers);
        this.authors = new ArrayList<>(song.authors);
        this.genres = new ArrayList<>(song.genres);
    }

    public Song() {
        this._id = "";
        this.title = "";
        this.lyric = "";
        this.is_premium = false;
        this.like_count = 0;
        this.mp3_url = "";
        this.image_url = "";
        this.singers = new ArrayList<>();
        this.authors = new ArrayList<>();
        this.genres = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", lyric='" + (lyric != null ? lyric.substring(0, Math.min(lyric.length(), 30)) + "..." : "No Lyrics") + '\'' +
                ", is_premium=" + is_premium +
                ", like_count=" + like_count +
                ", mp3_url='" + mp3_url + '\'' +
                ", image_url='" + image_url + '\'' +
                ", singers=" + getArtistNames(singers) +
                ", authors=" + getArtistNames(authors) +
                ", genres=" + getGenreNames(genres) +
                '}';
    }

    // Constructor
    public Song(String _id, String title, String lyrics, boolean is_premium, int like_count,
                String mp3_url, String image_url, List<Artist> authors, List<Artist> singers, List<Genre> genres) {
        this._id = _id;
        this.title = title;
        this.lyric = lyrics;
        this.is_premium = is_premium;
        this.like_count = like_count;
        this.mp3_url = mp3_url;
        this.image_url = image_url;
        this.singers = singers;
        this.authors = authors;
        this.genres = genres;
    }

    // Parcelable implementation
    public Song(Parcel in) {
        _id = in.readString();
        title = in.readString();
        lyric = in.readString();
        is_premium = in.readByte() != 0;
        like_count = in.readInt();
        mp3_url = in.readString();
        image_url = in.readString();
        singers = new ArrayList<>();
        in.readList(singers, Artist.class.getClassLoader());
        authors = new ArrayList<>();
        in.readList(authors, Artist.class.getClassLoader());
        genres = new ArrayList<>();
        in.readList(genres, Genre.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(title);
        dest.writeString(lyric);
        dest.writeByte((byte) (is_premium ? 1 : 0));
        dest.writeInt(like_count);
        dest.writeString(mp3_url);
        dest.writeString(image_url);
        dest.writeList(singers);
        dest.writeList(authors);
        dest.writeList(genres);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    // Getters
    public String getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getLyrics() {
        return lyric;
    }

    public boolean isPremium() {
        return is_premium;
    }

    public int getLikeCount() {
        return like_count;
    }

    public String getMp3Url() {
        return mp3_url;
    }

    public String getImageUrl() {
        return image_url;
    }

    private String getArtistNames(List<Artist> artists) {
        return (artists != null && !artists.isEmpty())
                ? artists.stream().map(Artist::getName).collect(Collectors.joining(", "))
                : "";
    }

    private String getGenreNames(List<Genre> genres) {
        return (genres != null && !genres.isEmpty())
                ? genres.stream().map(Genre::getName).collect(Collectors.joining(", "))
                : "";
    }

    public List<String> getSingerNames() {
        return (singers != null)
                ? singers.stream().map(Artist::getName).collect(Collectors.toList())
                : new ArrayList<>();
    }

    public String getSingersString() {
        return (singers != null && !singers.isEmpty())
                ? singers.stream().map(Artist::getName).collect(Collectors.joining(", "))
                : "";
    }

    public List<String> getAuthorNames() {
        return (authors != null)
                ? authors.stream().map(Artist::getName).collect(Collectors.toList())
                : new ArrayList<>();
    }

    public String getAuthorsString() {
        return (authors != null && !authors.isEmpty())
                ? authors.stream().map(Artist::getName).collect(Collectors.joining(", "))
                : "";
    }

    public List<String> getGenreNames() {
        return (genres != null)
                ? genres.stream().map(Genre::getName).collect(Collectors.toList())
                : new ArrayList<>();
    }

    public String getGenresString() {
        return (genres != null && !genres.isEmpty())
                ? genres.stream().map(Genre::getName).collect(Collectors.joining(", "))
                : "";
    }

    public String getSingerNameAt(int index) {
        return (singers != null && index >= 0 && index < singers.size())
                ? singers.get(index).getName()
                : "";
    }

    public String getSingerImageUrlAt(int index) {
        return (singers != null && index >= 0 && index < singers.size())
                ? singers.get(index).getAvatarUrl()
                : "";
    }

    public String getAuthorNameAt(int index) {
        return (authors != null && index >= 0 && index < authors.size())
                ? authors.get(index).getName()
                : "";
    }

    public String getAuthorImageUrlAt(int index) {
        return (authors != null && index >= 0 && index < authors.size())
                ? authors.get(index).getAvatarUrl()
                : "";
    }

    public String getAuthorBioAt(int index) {
        return (authors != null && index >= 0 && index < authors.size())
                ? authors.get(index).getDescription()
                : "";
    }

    public int getSingerFollowersAt(int index) {
        return (singers != null && index >= 0 && index < singers.size())
                ? singers.get(index).getFollowers()
                : 0;
    }

    public int getAuthorFollowersAt(int index) {
        return (authors != null && index >= 0 && index < authors.size())
                ? authors.get(index).getFollowers()
                : 0;
    }

    public String getSingerBioAt(int index) {
        return (singers != null && index >= 0 && index < singers.size())
                ? singers.get(index).getDescription()
                : "";
    }

    // Setters
    public void setId(String _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLyrics(String lyrics) {
        this.lyric = lyrics;
    }

    public void setPremium(boolean is_premium) {
        this.is_premium = is_premium;
    }

    public void setLikeCount(int like_count) {
        this.like_count = like_count;
    }

    public void setMp3Url(String mp3_url) {
        this.mp3_url = mp3_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public String getSingerIdAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getId() : null;
    }

    public List<Artist> getSingers() {
        return singers;
    }
    public void setSingers(List<Artist> singers) {
        this.singers = singers;
    }
    public List<Artist> getAuthors() {
        return authors;
    }
    public void setAuthors(List<Artist> authors) {
        this.authors = authors;
    }
    public List<Genre> getGenres() {
        return genres;
    }
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setIs_premium(boolean b) {
        this.is_premium = b;
    }
}