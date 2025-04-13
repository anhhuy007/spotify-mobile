package com.example.spotifyclone.features.chatbot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.chatbot.model.ChatMessage;

import java.util.List;

import io.noties.markwon.Markwon;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_AI = 1;
    private static final int TYPE_LOADING = 2;
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_USER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
                return new UserViewHolder(view);
            }
            case TYPE_AI: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bot, parent, false);
                return new AIViewHolder(view);
            }
            default: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_typing, parent, false);
                return new TypingViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        Context context = holder.itemView.getContext();
        Markwon markwon = Markwon.create(context);

        if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.userMessageText.setVisibility(View.VISIBLE);
            markwon.setMarkdown(userHolder.userMessageText, message.getMessage());
        } else if (holder instanceof AIViewHolder) {
            AIViewHolder aiHolder = (AIViewHolder) holder;
            aiHolder.aiMessageText.setVisibility(View.VISIBLE);
            markwon.setMarkdown(aiHolder.aiMessageText, message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageText;

        UserViewHolder(View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.textMessage);
        }
    }

    static class AIViewHolder extends RecyclerView.ViewHolder {
        TextView aiMessageText;

        AIViewHolder(View itemView) {
            super(itemView);
            aiMessageText = itemView.findViewById(R.id.textMessage);
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView animationView;

        TypingViewHolder(View itemView) {
            super(itemView);
            animationView = itemView.findViewById(R.id.lottieTyping);
        }
    }
}
