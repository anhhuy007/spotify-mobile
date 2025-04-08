package com.example.spotifyclone.features.library.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.library.viewModel.LibraryPlaylistsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class NewPlaylistDialog extends BottomSheetDialogFragment {

    private String songName;
    private String songImage;
    private EditText playlistNameEditText;
    private Button createButton;
    private ImageButton exit;
    private LibraryPlaylistsViewModel playlistViewModel;




    public static NewPlaylistDialog newInstance(String songName, String song_image) {
        NewPlaylistDialog fragment = new NewPlaylistDialog();
        Bundle args = new Bundle();
        args.putString("song_name", songName);
        args.putString("song_image", song_image);
        fragment.setArguments(args); // Đặt dữ liệu vào arguments
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songName = getArguments().getString("song_name");
            songImage=getArguments().getString("song_image");
        }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.bottom_sheet_new_playlist, container, false);
        playlistNameEditText=view.findViewById(R.id.playlist_name_edittext);
        createButton=view.findViewById(R.id.create_button);
        exit=view.findViewById(R.id.close_button);

        // set up viewmodel
//        setupViewModel();


        // Handle click create new playlist
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlistName=playlistNameEditText.getText().toString();
                if(playlistName==null||playlistName.isEmpty()){
                    playlistViewModel.createPlaylist( songName, songImage);
                }
                else{
                    playlistViewModel.createPlaylist( songName,songImage);
                }
                dismiss();
            }
        });
        playlistNameEditText.setHint(songName);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }


    private void setupViewModel() {
        playlistViewModel = new ViewModelProvider(this).get(LibraryPlaylistsViewModel.class);


    }

    public void setViewModel(LibraryPlaylistsViewModel viewModel) {
        this.playlistViewModel = viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null) {
            View parentView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (parentView != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parentView);

                // Lấy chiều cao màn hình
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;

                // Giới hạn chiều cao của BottomSheet là 70% màn hình
                ViewGroup.LayoutParams layoutParams = parentView.getLayoutParams();
                layoutParams.height = (int) (screenHeight * 0.7);
                parentView.setLayoutParams(layoutParams);

                // Mở rộng BottomSheet ngay từ đầu
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);


            }
        }

        // Hiển thị bàn phím tự động
        showKeyboard();
    }

    private void showKeyboard() {
        playlistNameEditText.requestFocus(); // Đặt con trỏ vào EditText
        playlistNameEditText.post(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(playlistNameEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }




}
