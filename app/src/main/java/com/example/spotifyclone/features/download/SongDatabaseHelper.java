package com.example.spotifyclone.features.download;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.player.model.song.Song;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;
public class SongDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SongDatabaseHelper";
    private static final String DATABASE_NAME = "songs.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_SONGS = "songs";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LYRIC = "lyric";
    private static final String COLUMN_IS_PREMIUM = "is_premium";
    private static final String COLUMN_LIKE_COUNT = "like_count";
    private static final String COLUMN_MP3_URL = "mp3_url";  // Will store local path after download
    private static final String COLUMN_IMAGE_URL = "image_url";  // Will store local path after download
    private static final String COLUMN_SINGERS = "singers";
    private static final String COLUMN_AUTHORS = "authors";
    private static final String COLUMN_GENRES = "genres";
    private static final String COLUMN_SHOULD_PLAY_AD = "should_play_ad";
    private static final String COLUMN_PLAN_TYPES = "plan_types";

    private final Context context;
    private final ExecutorService downloadExecutor = Executors.newFixedThreadPool(3);

    public SongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_SONGS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_LYRIC + " TEXT, " +
                COLUMN_IS_PREMIUM + " INTEGER, " +
                COLUMN_LIKE_COUNT + " INTEGER, " +
                COLUMN_MP3_URL + " TEXT, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_SINGERS + " TEXT, " +
                COLUMN_AUTHORS + " TEXT, " +
                COLUMN_GENRES + " TEXT" +
                COLUMN_SHOULD_PLAY_AD + "INTEGER" +
                COLUMN_PLAN_TYPES + "TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    // Interface to notify download progress and completion
    public interface DownloadCallback {
        void onProgressUpdate(int progress);
        void onDownloadComplete(Song song);
        void onError(String message);
    }

    // Download both MP3 and image files and save to local storage
    public void downloadSong(Song song, DownloadCallback callback) {
        downloadExecutor.execute(() -> {
            try {
                // Create directories if they don't exist
                File musicDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "offline_songs");
                File imageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "song_images");

                if (!musicDir.exists()) {
                    musicDir.mkdirs();
                }
                if (!imageDir.exists()) {
                    imageDir.mkdirs();
                }

                // Store the song object - we'll modify it later
                Song localSong = new Song(song);  // Assume Song has a copy constructor or create a deep copy

                // Download MP3 file
                String mp3Filename = song.getId() + ".mp3";
                File mp3File = new File(musicDir, mp3Filename);
                downloadFile(song.getMp3Url(), mp3File, 50, callback);

                // Replace MP3 URL with local path
                localSong.setMp3Url(mp3File.getAbsolutePath());

                // Download image file
                String imageFilename = song.getId() + ".jpg";
                File imageFile = new File(imageDir, imageFilename);
                downloadFile(song.getImageUrl(), imageFile, 50, callback);

                // Replace image URL with local path
                localSong.setImageUrl(imageFile.getAbsolutePath());

                // Save song to database with local paths
                saveSongToDatabase(localSong);

                callback.onDownloadComplete(localSong);
            } catch (IOException e) {
                callback.onError("Failed to download: " + e.getMessage());
            }
        });
    }

    // Helper method to download a file from URL
    private void downloadFile(String fileUrl, File outputFile, int weightProgress, DownloadCallback callback) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }

        int fileLength = connection.getContentLength();
        InputStream input = connection.getInputStream();
        FileOutputStream output = new FileOutputStream(outputFile);

        byte[] data = new byte[4096];
        long total = 0;
        int count;

        while ((count = input.read(data)) != -1) {
            total += count;
            if (fileLength > 0) {
                int progress = (int) (total * weightProgress / fileLength);
                callback.onProgressUpdate(progress);
            }
            output.write(data, 0, count);
        }

        output.close();
        input.close();
        connection.disconnect();
    }

    // Convert lists to JSON strings for storage
    private String convertArtistListToJson(List<Artist> artists) {
        if (artists == null || artists.isEmpty()) {
            return "[]";
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (Artist artist : artists) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("_id", artist.getId());
                jsonObject.put("name", artist.getName());
                // Add other artist properties as needed
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            return "[]";
        }
    }

    private String convertGenreListToJson(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "[]";
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (Genre genre : genres) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("_id", genre.get_id());
                jsonObject.put("name", genre.getName());
                // Add other genre properties as needed
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            return "[]";
        }
    }

    // Parse JSON strings back to lists
    private List<Artist> parseArtistsFromJson(String json) {
        List<Artist> artists = new ArrayList<>();
        if (json == null || json.equals("[]")) {
            return artists;
        }

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Artist artist = new Artist();
                artist.setId(jsonObject.getString("_id"));
                artist.setName(jsonObject.getString("name"));
                // Set other artist properties as needed
                artists.add(artist);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing artists from JSON: " + e.getMessage());
        }
        return artists;
    }

    private List<Genre> parseGenresFromJson(String json) {
        List<Genre> genres = new ArrayList<>();
        if (json == null || json.equals("[]")) {
            return genres;
        }

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Genre genre = new Genre();
                genre.set_id(jsonObject.getString("_id"));
                genre.setName(jsonObject.getString("name"));
                // Set other genre properties as needed
                genres.add(genre);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing genres from JSON: " + e.getMessage());
        }
        return genres;
    }

    // Save song to SQLite database
    public void saveSongToDatabase(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, song.getId());
        values.put(COLUMN_TITLE, song.getTitle());
        values.put(COLUMN_LYRIC, song.getLyrics());
        values.put(COLUMN_IS_PREMIUM, song.isPremium() ? 1 : 0);
        values.put(COLUMN_LIKE_COUNT, song.getLikeCount());
        values.put(COLUMN_MP3_URL, song.getMp3Url());  // Could be online URL or local path
        values.put(COLUMN_IMAGE_URL, song.getImageUrl());  // Could be online URL or local path
        values.put(COLUMN_SINGERS, convertArtistListToJson(song.getSingers()));
        values.put(COLUMN_AUTHORS, convertArtistListToJson(song.getAuthors()));
        values.put(COLUMN_GENRES, convertGenreListToJson(song.getGenres()));
        // Insert or replace if song already exists
        db.insertWithOnConflict(TABLE_SONGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Get all saved songs from database
    @SuppressLint("Range")
    public List<Song> getAllSavedSongs() {
        List<Song> songList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                song.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                song.setLyrics(cursor.getString(cursor.getColumnIndex(COLUMN_LYRIC)));
                song.setIs_premium(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PREMIUM)) == 1);
                song.setLikeCount(cursor.getInt(cursor.getColumnIndex(COLUMN_LIKE_COUNT)));
                song.setMp3Url(cursor.getString(cursor.getColumnIndex(COLUMN_MP3_URL)));
                song.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));

                // Parse JSON strings back to lists
                song.setSingers(parseArtistsFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_SINGERS))));
                song.setAuthors(parseArtistsFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHORS))));
                song.setGenres(parseGenresFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES))));

                songList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return songList;
    }

    // Get a specific song by ID
    @SuppressLint("Range")
    public Song getSongById(String songId) {
        // Use local database connection that will be closed at the end of this method
        SQLiteDatabase dbLocal = this.getReadableDatabase();

        Cursor cursor = dbLocal.query(TABLE_SONGS, null, COLUMN_ID + "=?",
                new String[]{songId}, null, null, null);

        Song song = null;

        if (cursor.moveToFirst()) {
            song = new Song();
            song.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            song.setLyrics(cursor.getString(cursor.getColumnIndex(COLUMN_LYRIC)));
            song.setIs_premium(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PREMIUM)) == 1);
            song.setLikeCount(cursor.getInt(cursor.getColumnIndex(COLUMN_LIKE_COUNT)));
            song.setMp3Url(cursor.getString(cursor.getColumnIndex(COLUMN_MP3_URL)));
            song.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));

            // Parse JSON strings back to lists
            song.setSingers(parseArtistsFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_SINGERS))));
            song.setAuthors(parseArtistsFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHORS))));
            song.setGenres(parseGenresFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES))));
        }

        cursor.close();
        dbLocal.close();
        return song;
    }

    // Check if a song exists in the database
    public boolean songExists(String songId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS,
                new String[]{COLUMN_ID},
                COLUMN_ID + "=?",
                new String[]{songId},
                null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return exists;
    }

    // Check if a song is downloaded (files exist locally)
    public boolean isSongDownloaded(String songId) {
        Song song = getSongById(songId);
        if (song == null) {
            return false;
        }

        // Check if MP3 file exists
        File mp3File = new File(song.getMp3Url());

        // Check if image file exists
        File imageFile = new File(song.getImageUrl());

        // Song is considered downloaded if both files exist
        return mp3File.exists() && imageFile.exists();
    }

    public void deleteSong(String songId, DownloadCallback callback) {
        downloadExecutor.execute(() -> {
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();

                // Use a modified version of getSongById that doesn't close the database
                Song song = getSongByIdInternal(db, songId);

                if (song != null) {
                    String mp3Path = song.getMp3Url();
                    String imagePath = song.getImageUrl();

                    int progress = 0;

                    if (mp3Path != null && mp3Path.startsWith("/")) {
                        File mp3File = new File(mp3Path);
                        if (mp3File.exists() && mp3File.delete()) {
                            progress += 50;
                            callback.onProgressUpdate(progress);
                        }
                    }

                    if (imagePath != null && imagePath.startsWith("/")) {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists() && imageFile.delete()) {
                            progress += 50;
                            callback.onProgressUpdate(progress);
                        }
                    }

                    db.delete(TABLE_SONGS, COLUMN_ID + "=?", new String[]{songId});
                    callback.onDownloadComplete(song);
                } else {
                    callback.onError("Song not found in local database.");
                }
            } catch (Exception e) {
                callback.onError("Error deleting song: " + e.getMessage());
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });
    }

    // Create a private method that doesn't close the database
    @SuppressLint("Range")
    private Song getSongByIdInternal(SQLiteDatabase db, String songId) {
        Cursor cursor = db.query(TABLE_SONGS, null, COLUMN_ID + "=?",
                new String[]{songId}, null, null, null);

        Song song = null;

        if (cursor.moveToFirst()) {
            song = new Song();
            song.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            song.setLyrics(cursor.getString(cursor.getColumnIndex(COLUMN_LYRIC)));
            song.setIs_premium(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PREMIUM)) == 1);
            song.setLikeCount(cursor.getInt(cursor.getColumnIndex(COLUMN_LIKE_COUNT)));
            song.setMp3Url(cursor.getString(cursor.getColumnIndex(COLUMN_MP3_URL)));
            song.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));

            // Parse JSON strings back to lists
            song.setSingers(parseArtistsFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_SINGERS))));
            song.setAuthors(parseArtistsFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHORS))));
            song.setGenres(parseGenresFromJson(cursor.getString(cursor.getColumnIndex(COLUMN_GENRES))));
        }

        cursor.close();
        return song;
    }
}