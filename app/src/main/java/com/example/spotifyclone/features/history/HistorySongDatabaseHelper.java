package com.example.spotifyclone.features.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistorySongDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "HistorySongDatabaseHelper";

    // Table and column names
    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_SONG_ID = "song_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static HistorySongDatabaseHelper instance;

    private final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();

    // Singleton pattern
    public static synchronized HistorySongDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HistorySongDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private HistorySongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_SONG_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TIMESTAMP + " INTEGER" + ")";
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void addSongToHistory(String songId) {
        downloadExecutor.execute(() -> {
            SQLiteDatabase db = null;
            Cursor cursor = null;

            try {
                db = getWritableDatabase();

                String checkQuery = "SELECT 1 FROM " + TABLE_HISTORY + " WHERE " + COLUMN_SONG_ID + " = ?";
                cursor = db.rawQuery(checkQuery, new String[]{songId});

                boolean exists = cursor.moveToFirst();
                cursor.close();

                if (exists) {
                    String deleteSql = "DELETE FROM " + TABLE_HISTORY + " WHERE " + COLUMN_SONG_ID + " = ?";
                    db.execSQL(deleteSql, new Object[]{songId});
                }

                String insertSql = "INSERT INTO " + TABLE_HISTORY + " (" + COLUMN_SONG_ID + ", " + COLUMN_TIMESTAMP + ") VALUES (?, ?)";
                long dateOnlyTimestamp = getStartOfDayTimestamp();
                db.execSQL(insertSql, new Object[]{songId, dateOnlyTimestamp});
            } catch (Exception e) {
                Log.e(TAG, "addSongToHistory: Error", e);
            } finally {
                if (cursor != null && !cursor.isClosed()) cursor.close();
            }
        });
    }

    public List<HistorySong> getHistorySongs() {
        List<HistorySong> historySongs = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            String sql = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String songId = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ID));
                    @SuppressLint("Range") long timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                    historySongs.add(new HistorySong(songId, timestamp));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getHistorySongs: Error", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return historySongs;
    }

    public List<String> getAllSavedSongIds() {
        List<String> songIds = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            String sql = "SELECT " + COLUMN_SONG_ID + " FROM " + TABLE_HISTORY;
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String songId = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ID));
                    songIds.add(songId);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllSavedSongIds: Error", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return songIds;
    }

    private long getStartOfDayTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}