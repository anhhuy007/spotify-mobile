package com.example.spotifyclone.shared.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.spotifyclone.shared.model.PlayerState;
import com.google.gson.Gson;

import java.util.Objects;

public class PlayerRepository {
    private static final String PREF_NAME = "player_prefs";
    private static final String KEY_PLAYER_STATE = "player_state";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public PlayerRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.gson = new Gson();
    }

    public synchronized void savePlayerState(PlayerState playerState) {
        try {
            String playerStateJson = gson.toJson(playerState);
            editor.putString(KEY_PLAYER_STATE, playerStateJson);
            editor.apply();
        } catch (Exception e) {
            Log.e("PlayerRepository", "❌ Lỗi khi lưu trạng thái", e);
        }
    }

    public synchronized PlayerState loadPlayerState() {
        try {
            String playerStateJson = sharedPreferences.getString(KEY_PLAYER_STATE, null);
            if (playerStateJson != null) {
                PlayerState restoredState = gson.fromJson(playerStateJson, PlayerState.class);

                return Objects.requireNonNullElseGet(restoredState, PlayerState::new);
            }
        } catch (Exception e) {
            Log.e("PlayerRepository", "❌ Lỗi khi khôi phục trạng thái", e);
        }

        return new PlayerState();
    }
}
