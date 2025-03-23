package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chatapp.R;
import com.example.chatapp.models.Message;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_LEFT = 0;  // Tin nhắn từ người khác
    private static final int VIEW_TYPE_RIGHT = 1; // Tin nhắn từ chính người dùng

    private final Context context;
    private List<Message> messageList = new ArrayList<>();;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        // Kiểm tra nếu người gửi là chính người dùng
        if (messageList.get(position).getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_RIGHT;
        } else {
            return VIEW_TYPE_LEFT;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.txtMessage.setText(message.getContent());

        if (getItemViewType(position) == VIEW_TYPE_LEFT) {
            String avatarUrl = (message.getAvatarUrl() != null && !message.getAvatarUrl().isEmpty())
                    ? message.getAvatarUrl()
                    : "drawable://" + R.drawable.avatar;

            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}
