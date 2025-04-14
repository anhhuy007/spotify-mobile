package com.example.spotifyclone.features.album.ui;

import static androidx.navigation.Navigation.findNavController;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.download.SongDatabaseHelper;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.features.playlist.ui.AllPlaylistBottomSheet;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumBottomSheet extends BottomSheetDialogFragment {
    private LinearLayout add_to_playlist, download_song;
    private SongService songService;
    private SongDatabaseHelper dbHelper;
    private TextView download_song_text;
    private ImageView download_song_icon;
    private User currentUser;

    public static AlbumBottomSheet newInstance(String id, String song_image, String song_name, List<String> artists_name) {
        AlbumBottomSheet fragment = new AlbumBottomSheet();
        Bundle args = new Bundle();
        args.putString("_id", id);
        args.putString("song_image", song_image);
        args.putString("song_name", song_name);
        if (artists_name == null) {
            artists_name = new ArrayList<>();
        }
        args.putStringArrayList("song_artist", new ArrayList<>(artists_name));
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumBottomSheet() {
        // Constructor mặc định
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initUser();

        this.songService = RetrofitClient.getClient(requireContext()).create(SongService.class);
        dbHelper = new SongDatabaseHelper(requireContext());  // Or your current Activity/Fragment context

        View view = inflater.inflate(R.layout.bottom_sheet_song_option, container, false);
        TextView song_name = view.findViewById(R.id.song_name);
        ImageView song_image = view.findViewById(R.id.song_cover);
        TextView song_artist = view.findViewById(R.id.song_artist);

        add_to_playlist = view.findViewById(R.id.add_to_playlist);

        // Download song option
        download_song = view.findViewById(R.id.option_download);
        download_song_text = view.findViewById(R.id.option_download_text);
        download_song_icon = view.findViewById(R.id.option_download_icon);

        Bundle bundle = getArguments();
        String id, image, name;
        List<String> artistList;

        if (bundle == null ) {
            AlbumBottomSheetArgs args = AlbumBottomSheetArgs.fromBundle(bundle);
            id = args.getId();
            image = args.getSongImage();
            name = args.getSongName();
            artistList = Arrays.asList(args.getSongArtist());
        } else {
            id = bundle.getString("_id", "");
            image = bundle.getString("song_image", "");
            name = bundle.getString("song_name", "");
            artistList = bundle.getStringArrayList("song_artist");
            if (artistList == null) artistList = new ArrayList<>();
        }

        Glide.with(this).load(image).into(song_image);
        song_name.setText(name);
        song_artist.setText(TextUtils.join(", ", artistList));

        String finalId = id;
        String finalImage = image;
        String finalName = name;
        if (currentUser != null && currentUser.isPremium()) {
            if (dbHelper.isSongDownloaded(finalId)) {

                download_song_text.setText("Xóa nhạc");
                download_song_icon.setImageResource(R.drawable.ic_close);
                download_song.setOnClickListener(v -> {
                    Log.d("DEBUG", "onClick: " + finalId);
                    Log.d("DEBUG", "onClick: " + finalName);
                    dbHelper.deleteSong(finalId, new SongDatabaseHelper.DownloadCallback() {
                        final Handler mainHandler = new Handler(Looper.getMainLooper());

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onProgressUpdate(int progress) {
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    download_song_text.setText("Deleting... " + progress + "%");
                                }
                            });
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDownloadComplete(Song localSong) {
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    download_song_text.setText("Tải nhạc");
                                    download_song_icon.setImageResource(R.drawable.ic_download);
                                    Toast.makeText(requireContext(), "Song deleted!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String message) {
                            mainHandler.post(() -> {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "Download failed: " + message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    });
                });

            } else {
                download_song_text.setText("Tải nhạc");
                download_song_icon.setImageResource(R.drawable.ic_download);
                download_song.setOnClickListener(v -> {
                    songService.getSongById(finalId).enqueue(new Callback<APIResponse<Song>>() {
                        @Override
                        public void onResponse(Call<APIResponse<Song>> call, Response<APIResponse<Song>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Song song = response.body().getData();
//                                Log.d("DEBUG", "onResponse: " + song.toString());
                                dbHelper.downloadSong(song, new SongDatabaseHelper.DownloadCallback() {
                                    final Handler mainHandler = new Handler(Looper.getMainLooper());

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onProgressUpdate(int progress) {
                                        mainHandler.post(() -> {
                                            if (isAdded()) {
                                                download_song_text.setText("Downloading... " + progress + "%");
                                            }
                                        });
                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDownloadComplete(Song localSong) {
                                        mainHandler.post(() -> {
                                            if (isAdded()) {
                                                download_song_text.setText("Tải nhạc thành công");
                                                download_song_icon.setImageResource(R.drawable.ic_checkmark);
                                                Toast.makeText(requireContext(), "Song saved to device!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    @Override
                                    public void onError(String message) {
                                        mainHandler.post(() -> {
                                            if (isAdded()) {
                                                Toast.makeText(requireContext(), "Download failed: " + message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                });

                            } else {
                                Log.d("DEBUG Download", "onFailure: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<APIResponse<Song>> call, Throwable t) {
                            Log.d("DEBUG fetch" , "onFailure: " + t.getMessage());
                        }
                    });
                });
            }
        } else {
            download_song_text.setText("Nâng cấp lên Premium để tải nhạc");
            download_song.setAlpha(0.5f);
            download_song_icon.setAlpha(0.5f);
            download_song.setEnabled(false);
            download_song.setClickable(false);
        }

        add_to_playlist.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            dismiss();

            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    AllPlaylistBottomSheet allPlaylistBottomSheet =
                            AllPlaylistBottomSheet.newInstance(finalId, finalName, finalImage);
                    allPlaylistBottomSheet.show(fragmentManager, "AllPlaylistBottomSheet");
                } catch (Exception e) {
                    Log.e("AlbumBottomSheet", "Error showing AllPlaylistBottomSheet", e);
                }
            });
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View parentView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (parentView != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parentView);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;
            int peekHeight = screenHeight / 2;

            behavior.setPeekHeight(peekHeight);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setFitToContents(true);
            behavior.setHalfExpandedRatio(0.5f);
            behavior.setHideable(false);

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        bottomSheet.setLayoutParams(layoutParams);
                    } else if (newState == BottomSheetBehavior.STATE_DRAGGING ||
                            newState == BottomSheetBehavior.STATE_SETTLING) {
                        behavior.setPeekHeight(peekHeight);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // No-op
                }
            });
        }
    }

    private void initUser() {
        AuthRepository authRepository = new AuthRepository(requireContext());
        currentUser = authRepository.getUser();
    }

}
