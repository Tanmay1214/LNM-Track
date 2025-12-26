package com.tanmaybuilds.lnmtrack.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanmaybuilds.lnmtrack.DataModels.ChatMessage;
import com.tanmaybuilds.lnmtrack.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSentBy().equals(ChatMessage.SENT_BY_USER)) return 0;
        else return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_bot, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).textView.setText(message.getMessage());
        } else {
            ((BotViewHolder) holder).textView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        UserViewHolder(View itemView) { super(itemView); textView = itemView.findViewById(R.id.user_msg_text); }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        BotViewHolder(View itemView) { super(itemView); textView = itemView.findViewById(R.id.bot_msg_text); }
    }
}
