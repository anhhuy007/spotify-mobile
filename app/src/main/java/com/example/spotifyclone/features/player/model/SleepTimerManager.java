package com.example.spotifyclone.features.player.model;
import android.os.CountDownTimer;
import java.util.Locale;

public class SleepTimerManager {
    private static SleepTimerManager instance;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isTimerRunning;
    private OnTimerFinishedListener onTimerFinishedListener;

    public interface OnTimerFinishedListener {
        void onTimerFinished();
    }

    private SleepTimerManager() {
        isTimerRunning = false;
    }

    public static synchronized SleepTimerManager getInstance() {
        if (instance == null) {
            instance = new SleepTimerManager();
        }
        return instance;
    }

    public void setOnTimerFinishedListener(OnTimerFinishedListener listener) {
        this.onTimerFinishedListener = listener;
    }

    public void startTimer(long durationInMinutes) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timeLeftInMillis = durationInMinutes * 60 * 1000;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                if (onTimerFinishedListener != null) {
                    onTimerFinishedListener.onTimerFinished();
                }
            }
        }.start();

        isTimerRunning = true;
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public String getRemainingTimeFormatted() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}