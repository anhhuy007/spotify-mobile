package com.example.spotifyclone.features.chatbot.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.chatbot.model.ChatbotResponse;
import com.example.spotifyclone.features.chatbot.network.ChatbotService;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.network.PlaylistService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotViewModel extends ViewModel {
    private final ChatbotService chatbotService;



    private final MutableLiveData<String> responseLiveData = new MutableLiveData<>();


//    private  String responseMessage;
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage=new MutableLiveData<>();
    public ChatBotViewModel(ChatbotService chatbotService){
        this.chatbotService=chatbotService;
    }
    public void fetchChatbotResponse(String message) {
        isLoading.setValue(true);
        chatbotService.getChatbotResponse(message).enqueue(new Callback<APIResponse<String>>() {
            @Override
            public void onResponse(Call<APIResponse<String>> call, Response<APIResponse<String>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
//                        responseLiveData.setValue(response.body().getData());
//                        responseMessage=response.body().getData();
                        responseLiveData.setValue(response.body().getData());
                    } else {
                        errorMessage.setValue("Failed to load chatbot");
                        Log.e("ChatBotViewModel", "API Response not successful");
                    }
                } else {
                    Log.e("ChatBotViewModel", "API Response failed");
                    if (response.errorBody() != null) {
                        try {
                            Log.e("ChatBotViewModel", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("ChatBotViewModel", "Error parsing errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse<String>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("ChatBotViewModel", "API Call failed: " + t.getMessage(), t);
            }
        });


    }


    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public LiveData<String> getResponseLiveData() {
        return responseLiveData;
    }
//    public String getResponseLiveData() {
//        return responseMessage;
//    }

}
