package com.example.spotifyclone.features.chatbot.network;

import com.example.spotifyclone.features.chatbot.model.ChatbotResponse;
import com.example.spotifyclone.shared.model.APIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChatbotService {
    @GET("/chatbot/ask")
    Call<APIResponse<String>> getChatbotResponse(@Query("ask") String ask);
}
