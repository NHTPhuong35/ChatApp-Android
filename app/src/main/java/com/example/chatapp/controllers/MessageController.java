package com.example.chatapp.controllers;

import static com.example.chatapp.utils.Constants.MESSAGES_NODE;
import static com.example.chatapp.utils.Constants.USERS_NODE;

import android.util.Log;

import com.example.chatapp.adapters.MessageAdapter;
import com.example.chatapp.models.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageController {
    private DatabaseReference databaseRef, usersRef;
    private List<Message> messageList = new ArrayList<>();
    private AtomicInteger pendingMessages = new AtomicInteger(0);

    public MessageController() {
        databaseRef = FirebaseDatabase.getInstance().getReference(MESSAGES_NODE);
        usersRef = FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    public void sendMessage(String senderId, String content, String type, String attachmentUrl) {
        if (content == null || content.trim().isEmpty()) {
            Log.e("MessageController", "Tin nhắn rỗng, không thể gửi.");
            return;
        }

        String messageId = databaseRef.push().getKey();
        if (messageId == null) {
            Log.e("MessageController", "Lỗi: Không thể tạo ID cho tin nhắn.");
            return;
        }

        Message message = new Message(messageId, senderId, content, type, attachmentUrl, null, false);

        databaseRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    Log.i("MessageController", "Tin nhắn đã được gửi thành công!");
                    databaseRef.child(messageId).child("createdAt").setValue(ServerValue.TIMESTAMP);
                })
                .addOnFailureListener(e -> Log.e("MessageController", "Lỗi khi gửi tin nhắn: " + e.getMessage()));
    }

    // Lấy tin nhắn và thông tin người dùng
    public void fetchMessagesWithUserData(MessageListener listener) {
        databaseRef.orderByChild("createdAt").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    pendingMessages.incrementAndGet();
                    fetchUserDetails(message, listener);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
//                Message updatedMessage = snapshot.getValue(Message.class);
//                if (updatedMessage != null) {
//                    fetchUserDetails(updatedMessage, listener, true);
//                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Message removedMessage = snapshot.getValue(Message.class);
//                if (removedMessage != null) {
//                    listener.onMessageRemoved(removedMessage);
//                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Không cần xử lý di chuyển tin nhắn trong trường hợp này
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy tin nhắn: " + error.getMessage());
            }
        });
    }

    // Lấy thông tin người dùng nhanh chóng và cập nhật vào tin nhắn
    private void fetchUserDetails(Message message, MessageListener listener) {
        usersRef.child(message.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("userName").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);

                    message.setUserName(userName != null ? userName : "Unknown");
                    message.setAvatarUrl(avatarUrl != null ? avatarUrl : "");
                    message.setStatus(status != null ? status : "Offline");
                }
                Log.i("MessageController", "Tin nhắn mới: " + message.getContent());
                if (pendingMessages.decrementAndGet() == 0) {
                    // Khi tất cả tin nhắn đã được xử lý, trả về đúng thứ tự
                    for (Message msg : messageList) {
                        listener.onMessageAdded(msg);
                    }
                    messageList.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy thông tin người dùng: " + error.getMessage());
            }
        });
    }


    // Interface callback
    public interface MessageListener {
//        void onMessagesReceived(List<Message> messages);
        void onMessageAdded(Message message);
    }
}
