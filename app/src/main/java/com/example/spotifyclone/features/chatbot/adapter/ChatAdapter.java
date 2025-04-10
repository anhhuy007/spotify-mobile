package com.example.spotifyclone.features.chatbot.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_AI = 1;

    private List<String> messages;

    public ChatAdapter(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu tin nhắn của người dùng, trả về TYPE_USER, còn lại là TYPE_AI
        return position % 2 == 0 ? TYPE_USER : TYPE_AI;  // Ví dụ: Dùng modulo để phân biệt
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbot_message, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbot_message, parent, false);
            return new AIViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String message = messages.get(position);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int maxWidth = (int) (screenWidth * 0.7);


        if (holder instanceof UserViewHolder) {

            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.userMessageText.setMaxWidth(maxWidth);

            userHolder.userMessageText.setText(message);
            userHolder.userMessageText.setVisibility(View.VISIBLE);
            userHolder.aiMessageText.setVisibility(View.GONE);
        } else if (holder instanceof AIViewHolder) {

            AIViewHolder aiHolder = (AIViewHolder) holder;
            aiHolder.aiMessageText.setMaxWidth(maxWidth);
            aiHolder.aiMessageText.setText(message);
            aiHolder.aiMessageText.setVisibility(View.VISIBLE);
            aiHolder.userMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }
    public void setData(List<String> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageText;
        TextView aiMessageText;

        UserViewHolder(View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            aiMessageText = itemView.findViewById(R.id.aiMessageText);

        }
    }

    static class AIViewHolder extends RecyclerView.ViewHolder {
        TextView aiMessageText;
        TextView userMessageText;


        AIViewHolder(View itemView) {
            super(itemView);
            aiMessageText = itemView.findViewById(R.id.aiMessageText);
            userMessageText = itemView.findViewById(R.id.userMessageText);

        }
    }
}
