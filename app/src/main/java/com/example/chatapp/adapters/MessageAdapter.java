package com.example.chatapp.adapters;

import static com.example.chatapp.utils.Constants.MESSAGE_TYPE_IMAGE;

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
    private List<Message> messageList = new ArrayList<>();
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        // Kiểm tra nếu người gửi là chính người dùng
        Message message = messageList.get(position);
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
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

        // Hiển thị ảnh nếu tin nhắn là ảnh
        if (MESSAGE_TYPE_IMAGE.equals(message.getType())) {
            holder.txtMessage.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(message.getContent()) // Content chứa URL ảnh
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgMessage);
        } else {
            // Hiển thị tin nhắn văn bản
            holder.imgMessage.setVisibility(View.GONE);
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setText(message.getContent());
        }

        // Avatar + tên người gửi
        if (getItemViewType(position) == VIEW_TYPE_LEFT) {
            String avatarUrl = (message.getAvatarUrl() != null && !message.getAvatarUrl().isEmpty())
                    ? message.getAvatarUrl()
                    : "drawable://" + R.drawable.avatar;

            holder.txtSenderName.setText(message.getUsername() != null ? message.getUsername() : "Người dùng");

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

    // Phương thức để thêm tin nhắn vào danh sách
    public void addMessage(Message message) {
        if (message != null) {
            messageList.add(message);
            notifyItemInserted(messageList.size() - 1);  // Chỉ thông báo thay đổi vị trí tin nhắn mới
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgMessage;
        TextView txtMessage, txtSenderName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            imgMessage = itemView.findViewById(R.id.imgMessage);
            txtSenderName = itemView.findViewById(R.id.txtSenderName); // nếu có tên người gửi
        }
    }
}
