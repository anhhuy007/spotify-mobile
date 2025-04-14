package com.example.spotifyclone.features.player.model.audio;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.download.SongDatabaseHelper;
import com.example.spotifyclone.features.player.model.playlist.PlayList;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.premium.network.PremiumService;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.features.notification.PlayerNotification;

public class MusicPlayerController {
    private static final String TAG = "MusicPlayerController";
    private final SongService songService;
    private static volatile MusicPlayerController instance;
    private final AudioPlayer audioPlayer;
    private final CopyOnWriteArrayList<PlaybackListener> externalListeners;
    private RepeatMode repeatMode;
    private ShuffleMode shuffleMode;
    private final Object playlistLock = new Object();
    private final PlayList playList;
    private boolean isStopAtEndOfTrack  = false;
    private volatile boolean isReleased;
    private final PlayerNotification playerNotification;
    private final Song adSong = new Song("", "Quảng cáo của Spotify", "", false, 0, String.valueOf(R.raw.ads), "https://res.cloudinary.com/dndmj9oid/image/upload/v1744005550/download_i9ti4i.jpg", null, null, null);
    private static final int REFILL_THRESHOLD = 1;
    private static final int REFILL_COUNT = 15;
    private MusicPlayerViewModel musicPlayerViewModel;
    private PremiumService premiumService;
    private final SongDatabaseHelper songDatabaseHelper;
    public void setStopAtEndOfTrack(boolean isOn){
        this.isStopAtEndOfTrack =isOn;
    }
    private static final int MAX_SONGS_TO_ADS = 3;
    private int count_ads = 0;
    private MusicPlayerController(@NonNull Context context) {
        Context applicationContext = context.getApplicationContext();
        this.audioPlayer = new AudioPlayer(applicationContext);
        this.playerNotification = new PlayerNotification(context);
        this.externalListeners = new CopyOnWriteArrayList<>();
        this.repeatMode = RepeatMode.REPEAT_OFF;
        this.shuffleMode = ShuffleMode.SHUFFLE_OFF;
        this.playList = new PlayList(List.of(), ShuffleMode.SHUFFLE_OFF);
        this.isReleased = false;
        songDatabaseHelper = new SongDatabaseHelper(getApplicationContext());
        this.songService = RetrofitClient.getClient(context).create(SongService.class);
        setupInternalPlaybackListener();
    }

    private Context getApplicationContext() {
        return SpotifyCloneApplication.getInstance().getApplicationContext();
    }

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

    public void attachViewModel(MusicPlayerViewModel viewModel) {
        this.musicPlayerViewModel = viewModel;
    }

    private void checkReleased() {
        if (isReleased) {
            throw new IllegalStateException("MusicPlayerController has been released");
        }
    }

    private void setupInternalPlaybackListener() {
        audioPlayer.setPlaybackListener(new PlaybackListener() {
            @Override
            public void onStarted(@NonNull Song song) {
                notifyPlaybackStarted(song);
                playerNotification.createMediaNotification(song, true, audioPlayer.getCurrentPosition(), audioPlayer.getDuration());
            }

            @Override
            public void onPaused(@NonNull Song song) {
                playerNotification.createMediaNotification(song, false, audioPlayer.getCurrentPosition(), audioPlayer.getDuration());
                notifyPlaybackPaused(song);
            }

            @Override
            public void onCompleted(@NonNull Song song) {
                if (Boolean.TRUE.equals(musicPlayerViewModel.isAdPlaying().getValue())) {
                    musicPlayerViewModel.setAdPlaying(false);
                }
                handleSongCompletion();
            }

            @Override
            public void onError(@NonNull Song song, @NonNull String error) {
                handlePlaybackError(error);
                musicPlayerViewModel.playLocalSongs(null);
            }
        });
    }

    public void addPlaybackListener(@NonNull PlaybackListener listener) {
        checkReleased();
        externalListeners.add(listener);
    }

    public void removePlaybackListener(@NonNull PlaybackListener listener) {
        checkReleased();
        externalListeners.remove(listener);
    }

    private void notifyPlaybackStarted(@NonNull Song song) {
        for (PlaybackListener listener : externalListeners) {
            try {
                listener.onStarted(song);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener of playback start", e);
            }
        }
    }

    private void notifyPlaybackPaused(@NonNull Song song) {
        for (PlaybackListener listener : externalListeners) {
            try {
                listener.onPaused(song);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener of playback pause", e);
            }
        }
    }

    private void notifyPlaybackError(@NonNull String error) {
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

    public void playSong(Song song) {
        checkReleased();
        if (song == playList.getCurrentSong()) {
            play();
            return;
        }
        synchronized (playlistLock) {
            playList.insertSong(song);
            play();
        }
    }

    private void checkAndFetchMoreSongs() {
        synchronized (playlistLock) {
            if (playList.getRemainingSongs() <= REFILL_THRESHOLD || playList.getCurrentSong() == null || playList.isEmpty()) {
                fetchMoreSongsAndPlayNext();
            }
        }
    }

    private void handleSongCompletion() {
        synchronized (playlistLock) {
            checkAndFetchMoreSongs();
            if (isStopAtEndOfTrack){
                isStopAtEndOfTrack = false;
            }
            else if (repeatMode == RepeatMode.REPEAT_INFINITE) {
                play();
            } else {
                playNextSong();
            }
        }
    }

    private void handlePlaybackError(String error) {
        notifyPlaybackError(error);
        playNextSong();
    }

//    public void playPlaylist(PlayList newPlayList) {
//        checkReleased();
//        synchronized (playlistLock) {
//            playList.insertPlaylist(newPlayList);
//            checkAndFetchMoreSongs();
//            play();
//        }
//    }

    public void play() {
        checkReleased();
        if(Boolean.TRUE.equals(musicPlayerViewModel.isAdPlaying().getValue())) return;
        Song currentSong = playList.getCurrentSong();
        if (currentSong != null) {
            audioPlayer.loadAndPlay(currentSong);
        } else {
            checkAndFetchMoreSongs();
        }
    }

    public void pause() {
        checkReleased();
        audioPlayer.pause();
    }

    public void stop() {
        checkReleased();
        audioPlayer.stop();
    }


    public void continuePlaying() {
        checkReleased();
        audioPlayer.play();
    }

    public void seekTo(long position) {
        checkReleased();
        audioPlayer.seekTo(position);
    }

    public long getDuration() {
        checkReleased();
        return (int) audioPlayer.getDuration();
    }
    public long getCurrentDuration() {
        checkReleased();
        return (int) audioPlayer.getCurrentDuration();
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
            AuthRepository authRepository = new AuthRepository(getApplicationContext());
            User currentUser = authRepository.getUser();

            if (currentUser == null) {
                Log.e(TAG, "Current user is null");
                return false;
            }
            
            if(playList.checkNextSong()) {
                if (!currentUser.isPremium()) {
                    count_ads++;
                    if (count_ads >= MAX_SONGS_TO_ADS) {
                        count_ads = 0;
                        audioPlayer.loadAndPlayOffline(adSong);
                        musicPlayerViewModel.setAdPlaying(true);
                        return true;
                    }
                }
            }
            Song nextSong = playList.moveToNextSong();
            if (nextSong != null) {
                audioPlayer.loadAndPlay(nextSong);
                return true;
            } else {
                if(musicPlayerViewModel.getPlayType().getValue() == MusicPlayerViewModel.PlaybackSourceType.LOCAL) {
                    fetchLocalSongsAndPlayNext();
                }
                else {
                    fetchMoreSongsAndPlayNext();
                }
                return false;
            }
        }
    }

    private void fetchLocalSongsAndPlayNext() {
        List<Song> localSongs;
        if(songDatabaseHelper.getAllSavedSongs() != null) {
            localSongs = songDatabaseHelper.getAllSavedSongs();
        } else {
            localSongs = new ArrayList<>();
        }
        playList.addSongs(localSongs);
        playNextSong();
    }

    public boolean playPreviousSong() {
        checkReleased();
        synchronized (playlistLock) {
            Song previousSong = playList.getPreviousSong();
            if (previousSong != null) {
                audioPlayer.loadAndPlay(previousSong);
                return true;
            }
            return false;
        }
    }

    private void fetchMoreSongsAndPlayNext() {
        songService.getRandomSongs(REFILL_COUNT).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.addSongs(songs);
                            playNextSong();
                        }
                    } else {
                        Log.w(TAG, "No songs found in API response");
                    }
                } else {
                    Log.e("DEBUG", "onFailure: "+ response.message());

                }
            }
            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());            }
        });
    }

    public void close() {
        playerNotification.cancelNotification();
        playerNotification.release();
        synchronized (MusicPlayerController.class) {
            if (!isReleased) {
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


    public void setRepeatMode(RepeatMode nextMode) {
        checkReleased();
        repeatMode = nextMode;
    }

    public void setShuffle(ShuffleMode nextMode) {
        checkReleased();
        shuffleMode = nextMode;
        if (shuffleMode == ShuffleMode.SHUFFLE_ON) {
            playList.shuffle();
        }
    }


    public boolean isReleased() {
        return isReleased;
    }

    public void prioritizeSong(Song song) {
        checkReleased();
        playList.prioritizeSong(song);
        play();
    }

    private void fetchAlbumSongWithPriority(String id, String first_song_id) {
        songService.getAlbumSongs(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
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
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found in API response");
                    }
                } else {
                    Log.e("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void fetchAlbumSongs(String id) {
        songService.getAlbumSongs(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (!songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();
                            playList.addSongs(songs);
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found in API response");
                    }
                } else {
                    Log.e("DEBUG", "onFailure: " + response.message());

                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void fetchArtistSongWithPriority(String id, String first_song_id) {
        songService.getArtistSongs(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
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
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found for artist in API response");
                    }
                    Log.e("DEBUG", "Artist Songs: " + response.body());
                } else {
                    Log.e("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }


    private void fetchArtistSongs(String id) {
        songService.getArtistSongs(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();
                            playList.addSongs(songs);
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found for artist in API response");
                    }
                } else {
                    Log.e("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void setSongs(List<Song> upcomingSongs, int currentPosition) {
        playList.addSongs(upcomingSongs);
        playFrom(currentPosition);
    }

    public void playFrom(int currentPosition) {
        checkReleased();
        Song currentSong = playList.getCurrentSong();
        if (currentSong != null) {
            audioPlayer.loadAndPlayFrom(currentSong, currentPosition);
        } else {
            checkAndFetchMoreSongs();
        }
    }

    public void playSongsFrom(String sourceId, MusicPlayerViewModel.PlaybackSourceType type,  String prioritizedSongId) {
        checkReleased();
        synchronized (playlistLock) {
            switch (type) {
                case ALBUM:
                    if(prioritizedSongId == null) {
                        fetchAlbumSongs(sourceId);
                    } else {
                        fetchAlbumSongWithPriority(sourceId, prioritizedSongId);
                    }
                    break;

                case ARTIST:
                    if(prioritizedSongId == null) {
                        fetchArtistSongs(sourceId);

                    } else {
                        fetchArtistSongWithPriority(sourceId, prioritizedSongId);
                    }

                case PLAYLIST:
                    if(prioritizedSongId == null) {
                        fetchPlaylistSongs(sourceId);

                    } else {
                        fetchPlaylistSongWithPriority(sourceId, prioritizedSongId);
                    }
            }
        }
    }
    public void playLocalSongs(Song prioritizedLocalSong) {
        List<Song> localSongs;
        if(songDatabaseHelper.getAllSavedSongs() != null) {
            localSongs = songDatabaseHelper.getAllSavedSongs();
        } else {
            localSongs = new ArrayList<>();
            return;
        }
        checkReleased();
        synchronized (playlistLock) {
            playList.clear();
            if (prioritizedLocalSong != null) {
                playList.addFirstSong(prioritizedLocalSong);

                List<Song> filteredSongs = new ArrayList<>();
                for (Song song : localSongs) {
                    if (!song.getId().equals(prioritizedLocalSong.getId())) {
                        filteredSongs.add(song);
                    }
                }

                playList.addSongs(filteredSongs);
                play();
            }
            else {
                playList.addSongs(localSongs);
                play();
            }
        }
    }

    private void fetchPlaylistSongWithPriority(String id, String first_song_id) {
        songService.getPlaylistSongs(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
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
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found for artist in API response");
                    }
                } else {
                    Log.e("DEBUG", "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void fetchPlaylistSongs(String id) {
        songService.getPlaylistSongs(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> songs = response.body().getData().getItems();
                    if (songs != null && !songs.isEmpty()) {
                        synchronized (playlistLock) {
                            playList.clear();
                            playList.addSongs(songs);
                            play();
                        }
                    } else {
                        Log.w(TAG, "No songs found for artist in API response");
                    }
                } else {
                    Log.e("DEBUG", "onFailure: " + response.message());
                }
            }


            @Override
            public void onFailure(@NonNull Call<APIResponse<PaginatedResponse<Song>>> call, @NonNull Throwable t) {
                Log.e("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }


    public interface FetchSongsCallback {
        void onSongsFetched(List<Song> songs);
    }

    private void fetchSearchSongs(List<String> song_ids, String first_song_id, FetchSongsCallback callback) {
        if (song_ids == null || song_ids.isEmpty()) {
            callback.onSongsFetched(new ArrayList<>());
            return;
        }

        List<Song> result = new ArrayList<>();
        AtomicInteger pendingCalls = new AtomicInteger(song_ids.size());

        for (String id : song_ids) {
            Call<APIResponse<Song>> call = songService.getSongById(id);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<APIResponse<Song>> call, @NonNull Response<APIResponse<Song>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Song song = response.body().getData();
                        synchronized (result) {
                            if(Objects.equals(song.getId(), first_song_id)) {
                                result.add(0, song);
                            } else {
                                result.add(song);
                            }
                        }
                    } else {
                        Log.e("DEBUG", "onFailure: " + response.message());
                    }

                    if (pendingCalls.decrementAndGet() == 0) {
                        callback.onSongsFetched(result);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<APIResponse<Song>> call, @NonNull Throwable t) {
                    if (pendingCalls.decrementAndGet() == 0) {
                        callback.onSongsFetched(result);
                    }
                }
            });
        }
    }

    public void playSearchSongs(List<String> song_ids, String first_song_id) {
        checkReleased();
        fetchSearchSongs(song_ids, first_song_id, songs -> {
            synchronized (playlistLock) {
                playList.clear();
                playList.addSongs(songs);
                play();
            }
        });
    }


}