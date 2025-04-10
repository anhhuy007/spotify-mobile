package com.example.spotifyclone.shared.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Arrays;
import java.util.List;

public class DominantColorExtractor {

    public interface ColorCallback {
        void onColorExtracted(int color);
    }

    public static void getDominantColor(Context context, String imageUrl, ColorCallback callback) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        extractDominantColor(resource, callback);
                    }

                    @Override
                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {
                    }
                });
    }

    private static void extractDominantColor(Bitmap bitmap, ColorCallback callback) {
        if (bitmap == null) {

            callback.onColorExtracted(Color.GRAY);
            return;
        }

        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int dominantColor = getNonBlackColor(palette);
                callback.onColorExtracted(dominantColor);
            } else {
                callback.onColorExtracted(Color.GRAY);
            }
        });
    }

    private static int getNonBlackColor(Palette palette) {
        List<Integer> colors = Arrays.asList(
                palette.getDominantColor(Color.TRANSPARENT),
                palette.getVibrantColor(Color.TRANSPARENT),
                palette.getDarkVibrantColor(Color.TRANSPARENT),
                palette.getLightVibrantColor(Color.TRANSPARENT),
                palette.getMutedColor(Color.TRANSPARENT),
                palette.getDarkMutedColor(Color.TRANSPARENT),
                palette.getLightMutedColor(Color.TRANSPARENT)
        );

        for (int color : colors) {
            if (color != Color.TRANSPARENT && !isBlack(color)) {
                return color;
            }
        }
        return Color.GRAY;
    }

    private static boolean isBlack(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return (red < 40 && green < 40 && blue < 40);
    }
}
