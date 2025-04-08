package com.example.spotifyclone.features.download;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.spotifyclone.features.player.model.song.LocalSong;
import com.example.spotifyclone.features.player.model.song.Song;

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

public class SongDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SongDatabaseHelper";
    private static final String DATABASE_NAME = "songs.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_SONGS = "local_songs";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LYRIC = "lyric";
    private static final String COLUMN_IS_PREMIUM = "is_premium";
    private static final String COLUMN_LIKE_COUNT = "like_count";
    private static final String COLUMN_MP3_URL = "mp3_url";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_ARTIST_NAMES = "artist_names";

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
                COLUMN_ARTIST_NAMES + " TEXT)";
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
        void onDownloadComplete(LocalSong localSong);
        void onError(String message);
    }

    // Download song files and save to local storage
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

                // Download MP3 file
                String mp3Filename = song.getId() + ".mp3";
                File mp3File = new File(musicDir, mp3Filename);
                downloadFile(song.getMp3Url(), mp3File, 50, callback);

                // Download image file
                String imageFilename = song.getId() + ".jpg";
                File imageFile = new File(imageDir, imageFilename);
                downloadFile(song.getImageUrl(), imageFile, 50, callback);

                // Create LocalSong and save to database
                LocalSong localSong = new LocalSong(song, mp3File.getAbsolutePath(), imageFile.getAbsolutePath());
                saveSongToDatabase(localSong);

                callback.onDownloadComplete(localSong);
            } catch (IOException e) {
                Log.e(TAG, "Error downloading song: " + e.getMessage());
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

    // Save song to SQLite database
    public void saveSongToDatabase(LocalSong song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, song.getId());
        values.put(COLUMN_TITLE, song.getTitle());
        values.put(COLUMN_LYRIC, song.getLyric());
        values.put(COLUMN_IS_PREMIUM, song.isPremium() ? 1 : 0);
        values.put(COLUMN_LIKE_COUNT, song.getLikeCount());
        values.put(COLUMN_MP3_URL, song.getMp3Url());
        values.put(COLUMN_IMAGE_URL, song.getImageUrl());
        values.put(COLUMN_ARTIST_NAMES, song.getArtistNames());

        // Insert or replace if song already exists
        db.insertWithOnConflict(TABLE_SONGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Get all local songs from database
    @SuppressLint("Range")
    public List<LocalSong> getAllLocalSongs() {
        List<LocalSong> songList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                LocalSong song = new LocalSong();
                song.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                song.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                song.setLyric(cursor.getString(cursor.getColumnIndex(COLUMN_LYRIC)));
                song.setPremium(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PREMIUM)) == 1);
                song.setLikeCount(cursor.getInt(cursor.getColumnIndex(COLUMN_LIKE_COUNT)));
                song.setMp3Url(cursor.getString(cursor.getColumnIndex(COLUMN_MP3_URL)));
                song.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                song.setArtistNames(cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_NAMES)));

                songList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return songList;
    }

    // Get a specific song by ID
    @SuppressLint("Range")
    public LocalSong getSongById(String songId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, null, COLUMN_ID + "=?",
                new String[]{songId}, null, null, null);

        LocalSong song = null;

        if (cursor.moveToFirst()) {
            song = new LocalSong();
            song.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            song.setLyric(cursor.getString(cursor.getColumnIndex(COLUMN_LYRIC)));
            song.setPremium(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PREMIUM)) == 1);
            song.setLikeCount(cursor.getInt(cursor.getColumnIndex(COLUMN_LIKE_COUNT)));
            song.setMp3Url(cursor.getString(cursor.getColumnIndex(COLUMN_MP3_URL)));
            song.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
            song.setArtistNames(cursor.getString(cursor.getColumnIndex(COLUMN_ARTIST_NAMES)));
        }

        cursor.close();
        db.close();
        return song;
    }

    // Delete a song from database and storage
    public boolean deleteSong(String songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        LocalSong song = getSongById(songId);

        if (song != null) {
            // Delete mp3 file
            File mp3File = new File(song.getMp3Url());
            if (mp3File.exists()) {
                mp3File.delete();
            }

            // Delete image file
            File imageFile = new File(song.getImageUrl());
            if (imageFile.exists()) {
                imageFile.delete();
            }

            // Delete from database
            db.delete(TABLE_SONGS, COLUMN_ID + "=?", new String[]{songId});
            db.close();
            return true;
        }

        db.close();
        return false;
    }
}