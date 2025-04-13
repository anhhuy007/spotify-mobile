package com.example.spotifyclone.features.player.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.artist.viewModel.ArtistListViewModel;
import com.example.spotifyclone.features.player.model.SleepTimerManager;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class SleepTimerDialog extends BottomSheetDialogFragment {
    private TextView btn5Min, btn10Min, btn15Min, btn30Min, btn45Min, btn1Hour, btnEndOfTrack, btnCancel;
    private TextView txtCurrentTimer;
//    private MediaPlayerService mediaPlayerService; // Giả sử bạn có service này để điều khiển nhạc
    private MusicPlayerViewModel musicPlayerViewModel;

    public static SleepTimerDialog newInstance() {
        return new SleepTimerDialog();
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Set the dialog to be full width
//        if (getDialog() != null) {
//            View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
//            if (bottomSheet != null) {
//                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
//                behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sleep_timer, container, false);

        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        musicPlayerViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);


        initViews(view);
        setupListeners();
        updateTimerStatus();

        return view;
    }

    private void initViews(View view) {
        btn5Min = view.findViewById(R.id.btn_5_min);
        btn10Min = view.findViewById(R.id.btn_10_min);
        btn15Min = view.findViewById(R.id.btn_15_min);
        btn30Min = view.findViewById(R.id.btn_30_min);
        btn45Min = view.findViewById(R.id.btn_45_min);
        btn1Hour = view.findViewById(R.id.btn_1_hour);
        btnEndOfTrack = view.findViewById(R.id.btn_end_of_track);
        btnCancel = view.findViewById(R.id.btn_cancel_timer);
        txtCurrentTimer = view.findViewById(R.id.txt_current_timer);
    }

    private void setupListeners() {
        btn5Min.setOnClickListener(v -> startTimer(5));
        btn10Min.setOnClickListener(v -> startTimer(10));
        btn15Min.setOnClickListener(v -> startTimer(15));
        btn30Min.setOnClickListener(v -> startTimer(30));
        btn45Min.setOnClickListener(v -> startTimer(45));
        btn1Hour.setOnClickListener(v -> startTimer(60));

        btnEndOfTrack.setOnClickListener(v -> {
            // Tính năng dừng khi kết thúc bài hát
//            mediaPlayerService.stopAtEndOfTrack(true);

            musicPlayerViewModel.setStopAtEndOfTrack(true);

            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            SleepTimerManager.getInstance().cancelTimer();
           musicPlayerViewModel.setStopAtEndOfTrack(true);
            updateTimerStatus();
        });
    }

    private void startTimer(int minutes) {
        SleepTimerManager.getInstance().setOnTimerFinishedListener(() -> {
//             Khi timer kết thúc, dừng nhạc

                musicPlayerViewModel.togglePlayPause();

                // Nếu bạn muốn hiển thị thông báo khi timer kết thúc
                showTimerFinishedNotification();

        });

        SleepTimerManager.getInstance().startTimer(minutes);
        updateTimerStatus();

        dismiss();
    }

    private void updateTimerStatus() {
        if (SleepTimerManager.getInstance().isTimerRunning()) {
            txtCurrentTimer.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            txtCurrentTimer.setText("Thời gian còn lại: " + SleepTimerManager.getInstance().getRemainingTimeFormatted());
        } else {
            txtCurrentTimer.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    }

    private void showTimerFinishedNotification() {
        if (getContext() != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "sleep_timer_channel")
                    .setSmallIcon(R.drawable.baseline_timer_24)
                    .setContentTitle("Hẹn giờ ngủ")
                    .setContentText("Đã tắt nhạc theo hẹn giờ")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//                notificationManager.notify(100, builder.build());
//            }
        }
    }
}