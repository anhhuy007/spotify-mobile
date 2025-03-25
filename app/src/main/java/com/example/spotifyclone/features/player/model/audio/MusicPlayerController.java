package com.example.spotifyclone.features.player.model.audio;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.notification.MusicNotificationManager;
import com.example.spotifyclone.features.player.model.playlist.PlayList;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.shared.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.example.spotifyclone.shared.model.APIResponse;

public class MusicPlayerController {
    private static final String TAG = "MusicPlayerController";
    private final SongService songService;
    private static volatile MusicPlayerController instance;
    private final AudioPlayer audioPlayer;
    private final Context applicationContext;
    private final CopyOnWriteArrayList<PlaybackListener> externalListeners;
    private RepeatMode repeatMode;
    private ShuffleMode shuffleMode;
    private final Object playlistLock = new Object();
    private PlayList playList;
    private String currentArtistId;
    private String currentAlbumId;
    private PlayList currentPlaylist;
    private volatile boolean isReleased;
    private static final int REFILL_THRESHOLD = 1;
    private static final int REFILL_COUNT = 15;

    /**
     * Private constructor to prevent direct instantiation.
     * This is part of the singleton pattern.
     */
    private MusicPlayerController(@NonNull Context context) {
        Log.d(TAG, "Initializing MusicPlayerController");
        this.applicationContext = context.getApplicationContext();
        this.audioPlayer = new AudioPlayer(applicationContext);
        this.externalListeners = new CopyOnWriteArrayList<>();
        this.repeatMode = RepeatMode.REPEAT_OFF;
        this.shuffleMode = ShuffleMode.SHUFFLE_OFF;
        this.playList = new PlayList(List.of(), ShuffleMode.SHUFFLE_OFF);
        this.isReleased = false;
        this.songService = RetrofitClient.getClient(context).create(SongService.class);
        setupInternalPlaybackListener();
    }

    /**
     * Gets the singleton instance of MusicPlayerController.
     * Creates a new instance if one doesn't exist or if the previous instance was released.
     *
     * @param context Application context
     * @return The singleton instance of MusicPlayerController
     */
    public static MusicPlayerController getInstance(@NonNull Context context) {
        if (instance == null || instance.isReleased) {
            synchronized (MusicPlayerController.class) {
                if (instance == null || instance.isReleased) {
                    instance = new MusicPlayerController(context);
                    instance.isReleased = false;
                }
            }
        }
        return instance;
    }

    /**
     * Checks if the controller has been released.
     * Throws an exception if it has, to prevent usage after release.
     */
    private void checkReleased() {
        if (isReleased) {
            throw new IllegalStateException("MusicPlayerController has been released");
        }
    }

    /**
     * Sets up the internal playback listener that handles events from the AudioPlayer.
     */
    private void setupInternalPlaybackListener() {
        audioPlayer.setPlaybackListener(new PlaybackListener() {
            @Override
            public void onStarted(@NonNull Song song) {
                notifyPlaybackStarted(song);
            }

            @Override
            public void onPaused(@NonNull Song song) {
                notifyPlaybackPaused(song);
            }

            @Override
            public void onCompleted(@NonNull Song song) {
                handleSongCompletion();
            }

            @Override
            public void onError(@NonNull Song song, @NonNull String error) {
                handlePlaybackError(error);
            }
        });
    }

    /**
     * Adds a listener to receive playback events.
     *
     * @param listener The listener to add
     */
    public void addPlaybackListener(@NonNull PlaybackListener listener) {
        checkReleased();
        Log.d(TAG, "Adding playback listener");
        externalListeners.add(listener);
    }

    /**
     * Removes a previously added playback listener.
     *
     * @param listener The listener to remove
     */
    public void removePlaybackListener(@NonNull PlaybackListener listener) {
        checkReleased();
        Log.d(TAG, "Removing playback listener");
        externalListeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that playback has started.
     *
     * @param song The song that started playing
     */
    private void notifyPlaybackStarted(@NonNull Song song) {
        Log.d(TAG, "Notifying playback started: " + song.getTitle());
        for (PlaybackListener listener : externalListeners) {
            try {
                listener.onStarted(song);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener of playback start", e);
            }
        }
    }

    /**
     * Notifies all registered listeners that playback has been paused.
     *
     * @param song The song that was paused
     */
    private void notifyPlaybackPaused(@NonNull Song song) {
        Log.d(TAG, "Notifying playback paused: " + song.getTitle());
        for (PlaybackListener listener : externalListeners) {
            try {
                listener.onPaused(song);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener of playback pause", e);
            }
        }
    }

    /**
     * Notifies all registered listeners that an error occurred during playback.
     *
     * @param error The error message
     */
    private void notifyPlaybackError(@NonNull String error) {
        Log.e(TAG, "Notifying playback error: " + error);
        for (PlaybackListener listener : externalListeners) {
            try {
                Song currentSong = playList.getCurrentSong();
                if (currentSong != null) {
                    listener.onError(currentSong, error);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener of playback error", e);
            }
        }
    }

    /**
     * Plays a specific song.
     *
     * @param song The song to play
     */
    public void playSong(Song song) {
        checkReleased();
        Log.d(TAG, "Attempting to play song: " + song.getTitle());
        if (song == playList.getCurrentSong()) {
            Log.d(TAG, "Song is already current, just playing");
            play();
            return;
        }
        synchronized (playlistLock) {
            playList.insertSong(song);
            play();
        }
    }

    /**
     * Checks if the playlist needs more songs and fetches them if necessary.
     */
    private void checkAndFetchMoreSongs() {
        synchronized (playlistLock) {
            Log.d(TAG, "Checking if more songs are needed");
            if (playList.getRemainingSongs() <= REFILL_THRESHOLD || playList.getCurrentSong() == null || playList.isEmpty()) {
                fetchMoreSongsAndPlayNext();

                // All artist album playlist will be null
                currentAlbumId = null;

            }
        }
    }

    /**
     * Handles song completion by either repeating the current song or playing the next one.
     */
    private void handleSongCompletion() {
        synchronized (playlistLock) {
            checkAndFetchMoreSongs();
            if (repeatMode == RepeatMode.REPEAT_INFINITE) {
                Log.d(TAG, "Repeating current song");
                play();
            } else {
                playNextSong();
            }
        }
    }

    /**
     * Handles playback errors by notifying listeners and attempting to play the next song.
     *
     * @param error The error message
     */
    private void handlePlaybackError(String error) {
        notifyPlaybackError(error);
        playNextSong();
    }

    /**
     * Plays a playlist by adding it to the current playlist and starting playback.
     *
     * @param newPlayList The playlist to play
     */
    public void playPlaylist(PlayList newPlayList) {
        checkReleased();
        synchronized (playlistLock) {
            Log.d(TAG, "Playing new playlist");
            playList.insertPlaylist(newPlayList);
            checkAndFetchMoreSongs();
            play();
        }
    }

    /**
     * Starts or resumes playback of the current song.
     */
    public void play() {
        checkReleased();
        Song currentSong = playList.getCurrentSong();
        if (currentSong != null) {
            Log.d(TAG, "Playing song: " + currentSong.getTitle());
            audioPlayer.loadAndPlay(currentSong);
        } else {
            Log.w(TAG, "No current song to play");
            checkAndFetchMoreSongs();
        }
    }

    /**
     * Pauses the current playback.
     */
    public void pause() {
        checkReleased();
        Log.d(TAG, "Pausing playback");
        audioPlayer.pause();
    }

    /**
     * Stops the current playback.
     */
    public void stop() {
        checkReleased();
        Log.d(TAG, "Stopping playback");
        audioPlayer.stop();
    }

    /**
     * Continues playing after a pause.
     */
    public void continuePlaying() {
        checkReleased();
        Log.d(TAG, "Continuing playback");
        audioPlayer.play();
    }

    /**
     * Seeks to a specific position in the current song.
     *
     * @param position The position to seek to, in milliseconds
     */
    public void seekTo(long position) {
        checkReleased();
        Log.d(TAG, "Seeking to position: " + position);
        audioPlayer.seekTo(position);
    }

    /**
     * Gets the duration of the current song.
     *
     * @return The duration in milliseconds
     */
    public long getDuration() {
        checkReleased();
        int duration = (int) audioPlayer.getDuration();
        return duration;
    }
    public long getCurrentDuration() {
        checkReleased();
        int duration = (int) audioPlayer.getCurrentDuration();
        return duration;
    }

    /**
     * Plays the next song in the playlist.
     */
    public List<Song> getUpcomingSongs() {
        checkReleased();
        synchronized (playlistLock) {
            return playList.getUpcomingSongs();
        }
    }
    public boolean playNextSong() {
        checkReleased();
        synchronized (playlistLock) {
            Log.d(TAG, "Playing next song");
            Song nextSong = playList.getNextSong();
            if (nextSong != null) {
                Log.d(TAG, "Playing next song: " + nextSong.getTitle());
                audioPlayer.loadAndPlay(nextSong);
                return true;
            } else {
                Log.w(TAG, "No next song available");
                fetchMoreSongsAndPlayNext();
                return false;
            }
        }
    }

    /**
     * Plays the previous song in the playlist.
     *
     * @return true if there was a previous song to play, false otherwise
     */
    public boolean playPreviousSong() {
        checkReleased();
        Log.d(TAG, "Playing previous song: ");
        synchronized (playlistLock) {
            Song previousSong = playList.getPreviousSong();
            if (previousSong != null) {
                Log.d(TAG, "Playing previous song: " + previousSong.getTitle());
                audioPlayer.loadAndPlay(previousSong);
                return true;
            }
            Log.w(TAG, "No previous song available");
            return false;
        }
    }

    /**
     * Fetches more songs from the API and plays the next song when available.
     */
    private void fetchMoreSongsAndPlayNext() {
        songService.getRandomSongs(REFILL_COUNT).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.addSongs(songs);
                            playList.printPlaylist();
                            playNextSong();
                        }
                    } else {
                        Log.w(TAG, "No songs found in API response");
                    }
                    Log.d("DEBUG","Popular Songs" + response.body());
                } else {
                    Log.d("DEBUG", "onFailure: "+ response.message());

                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                Log.d("DEBUG", "onFailure: " + t.getMessage());            }
        });
    }


    /**
     * Releases resources and marks this instance as released.
     * This method should be called when the application is shutting down
     * or when the controller is no longer needed.
     */
    public void close() {
        synchronized (MusicPlayerController.class) {
            if (!isReleased) {
                Log.d(TAG, "Releasing MusicPlayerController");
                try {
                    audioPlayer.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing audio player", e);
                } finally {
                    externalListeners.clear();
                    isReleased = true;
                }
            }
        }
}

    /**
     * Sets the repeat mode for playback.
     *
     * @param nextMode The repeat mode to set
     */
    public void setRepeatMode(RepeatMode nextMode) {
        checkReleased();
        repeatMode = nextMode;
    }

    /**
     * Sets the shuffle mode for playback.
     *
     * @param nextMode The shuffle mode to set
     */
    public void setShuffle(ShuffleMode nextMode) {
        checkReleased();
        shuffleMode = nextMode;
        if(shuffleMode == ShuffleMode.SHUFFLE_ON) {
            playList.shuffle();
        }
    }

    /**
     * Checks if the controller has been released.
     *
     * @return true if the controller has been released, false otherwise
     */
    public boolean isReleased() {
        return isReleased;
    }

    public void prioritizeSong(Song song) {
        checkReleased();
        playList.prioritizeSong(song);
        play();
    }

    public void playAlbum(String albumId){
        checkReleased();
        synchronized (playlistLock) {
            fetchAlbumSongs(albumId);
            Log.d("Album ID", albumId);
            currentAlbumId = albumId;
        }
    }

    public void playAlbumSong(String albumId, Song song) {
        checkReleased();
        synchronized (playlistLock) {
            fetchAlbumSongWithPriority(albumId, song);
            Log.d("Album ID", albumId);
            currentAlbumId = albumId;
        }
    }

    private void fetchAlbumSongWithPriority(String id, Song first_song) {
        songService.getAlbumSongs(id).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();
                            songs.removeIf(song -> song.getId().equals(first_song.getId()));
                            playList.addFirstSong(first_song);
                            playList.addSongs(songs);
                            playList.printPlaylist();
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found in API response");
                    }
                    Log.d("DEBUG", "Popular Songs: " + response.body());
                } else {
                    Log.d("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void fetchAlbumSongs(String id) {
        songService.getAlbumSongs(id).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();
                            for (Song song : songs) {
                                Log.d("SongInfo", "Title: " + song.getTitle() + ", Artist: " + song.getSingerNameAt(0));
                            }
                            playList.addSongs(songs);
                            playList.printPlaylist();

                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found in API response");
                    }
                    Log.d("DEBUG","Popular Songs" + response.body());
                } else {
                    Log.d("DEBUG", "onFailure: "+ response.message());

                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                Log.d("DEBUG", "onFailure: " + t.getMessage());            }
        });
    }

    private void fetchArtistSongWithPriority(String id, String first_song_id) {
        songService.getArtistSongs(id).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();

                            Song first_song = null;
                            for (Song song : songs) {
                                if (song.getId().equals(first_song_id)) {
                                    first_song = song;
                                    break;
                                }
                            }

                            if (first_song != null) {
                                songs.remove(first_song);
                                playList.addFirstSong(first_song);
                            }

                            playList.addSongs(songs);
                            playList.printPlaylist();
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found for artist in API response");
                    }
                    Log.d("DEBUG", "Artist Songs: " + response.body());
                } else {
                    Log.d("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }


    private void fetchArtistSongs(String id) {
        songService.getArtistSongs(id).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();
                            for (Song song : songs) {
                                Log.d("SongInfo", "Title: " + song.getTitle() + ", Artist: " + song.getSingerNameAt(0));
                            }
                            playList.addSongs(songs);
                            playList.printPlaylist();
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found for artist in API response");
                    }
                    Log.d("DEBUG", "Artist Songs: " + response.body());
                } else {
                    Log.d("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }


    public void playArtist(String artistId) {
        checkReleased();
        synchronized (playlistLock) {
            fetchArtistSongs(artistId);
            Log.d("Artist ID", artistId);
            currentArtistId = artistId;
        }
    }

    public void playArtistSong(String artistId, String songId) {
        checkReleased();
        synchronized (playlistLock) {
            fetchArtistSongWithPriority(artistId, songId);
            Log.d("Artist ID", artistId);
            currentArtistId = artistId;
        }
    }

}