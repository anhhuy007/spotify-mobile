package com.example.spotifyclone.features.chatbot.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.chatbot.network.ChatbotService;
import com.example.spotifyclone.shared.network.RetrofitClient;

public class ChatbotViewmodelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ChatbotViewmodelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatBotViewModel.class)) {
            ChatbotService chatbotService = RetrofitClient.getClient(context).create(ChatbotService.class);
            return (T) new ChatBotViewModel(chatbotService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
