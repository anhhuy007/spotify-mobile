//package com.example.spotifyclone.features.player.model.song;
//
//import android.os.Parcelable;
//
//import com.example.spotifyclone.features.artist.model.Artist;
//import com.example.spotifyclone.features.genre.model.Genre;
//
//import java.util.List;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//public class LocalSong implements Parcelable {
//    private String _id;
//    private String title;
//    private String lyric;
//    private boolean is_premium;
//    private int like_count;
//    private String mp3_url;
//    private String image_url;
//    private String artistNames;
//
//    public LocalSong() {}
//    public LocalSong(Song song, String localMp3Path, String localImagePath) {
//        this._id = song.getId();
//        this.title = song.getTitle();
//        this.lyric = song.getLyrics();
//        this.is_premium = song.isPremium();
//        this.like_count = song.getLikeCount();
//        this.mp3_url = localMp3Path;
//        this.image_url = localImagePath;
//        this.artistNames = song.getSingersString();
//    }
//
//        public String getId() { return _id; }
//    public void setId(String _id) { this._id = _id; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//    public String getLyric() { return lyric; }
//    public void setLyric(String lyric) { this.lyric = lyric; }
//
//    public boolean isPremium() { return is_premium; }
//    public void setPremium(boolean is_premium) { this.is_premium = is_premium; }
//
//    public int getLikeCount() { return like_count; }
//    public void setLikeCount(int like_count) { this.like_count = like_count; }
//
//    public String getMp3Url() { return mp3_url; }
//    public void setMp3Url(String mp3_url) { this.mp3_url = mp3_url; }
//
//    public String getImageUrl() { return image_url; }
//    public void setImageUrl(String image_url) { this.image_url = image_url; }
//
//    public String getArtistNames() { return artistNames; }
//    public void setArtistNames(String artistNames) { this.artistNames = artistNames; }
//
//
//    protected LocalSong(Parcel in) {
//        _id = in.readString();
//        title = in.readString();
//        lyric = in.readString();
//        is_premium = in.readByte() != 0; // true if byte != 0
//        like_count = in.readInt();
//        mp3_url = in.readString();
//        image_url = in.readString();
//        artistNames = in.readString();
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(_id);
//        dest.writeString(title);
//        dest.writeString(lyric);
//        dest.writeByte((byte) (is_premium ? 1 : 0));
//        dest.writeInt(like_count);
//        dest.writeString(mp3_url);
//        dest.writeString(image_url);
//        dest.writeString(artistNames);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<LocalSong> CREATOR = new Creator<LocalSong>() {
//        @Override
//        public LocalSong createFromParcel(Parcel in) {
//            return new LocalSong(in);
//        }
//
//        @Override
//        public LocalSong[] newArray(int size) {
//            return new LocalSong[size];
//        }
//    };
//}
