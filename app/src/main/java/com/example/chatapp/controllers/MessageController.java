package com.example.chatapp.controllers;

import static com.example.chatapp.utils.Constants.MESSAGES_NODE;
import static com.example.chatapp.utils.Constants.USERS_NODE;

import android.util.Log;

import com.example.chatapp.models.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageController {
    private DatabaseReference databaseRef, usersRef;
    private List<Message> messageList = new ArrayList<>();
    private AtomicInteger pendingMessages = new AtomicInteger(0);

    public MessageController() {
        // Khởi tạo các reference tới Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference(MESSAGES_NODE);
        usersRef = FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    public void sendMessage(String senderId, String content, String type) {
        if ((content == null || content.trim().isEmpty()) && !"image".equals(type)) {
            Log.e("MessageController", "Nội dung trống, không thể gửi.");
            return;
        }

        String messageId = databaseRef.push().getKey();
        if (messageId == null) return;

        // Lấy thông tin người dùng từ Firebase
        usersRef.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String username = "Người dùng"; // Giá trị mặc định
                String avatarUrl = ""; // Giá trị mặc định
                String status = "Offline"; // Giá trị mặc định

                // Kiểm tra người dùng có tồn tại trong Firebase không
                if (snapshot.exists()) {
                    username = snapshot.child("username").getValue(String.class);
                    avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                    status = snapshot.child("status").getValue(String.class);
                }

                // Tạo đối tượng Message và lưu vào Firebase
                Message message = new Message(
                        messageId, senderId, content, type, System.currentTimeMillis(), false, username, avatarUrl, status
                );

                // Lưu thông tin tin nhắn vào Firebase
                databaseRef.child(messageId).setValue(message)
                        .addOnSuccessListener(aVoid -> Log.i("MessageController", "Tin nhắn gửi thành công"))
                        .addOnFailureListener(e -> Log.e("MessageController", "Lỗi gửi tin: " + e.getMessage()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy thông tin người dùng: " + error.getMessage());
            }
        });
    }

    // Interface callback
    public interface MessageListener {
        void onMessageAdded(Message message);
    }

    // Lấy tin nhắn và thông tin người dùng
    public void fetchMessagesWithUserData(MessageListener listener) {
        databaseRef.orderByChild("createdAt").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    pendingMessages.incrementAndGet();
                    fetchUserDetails(message, listener);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // Không cần xử lý khi tin nhắn thay đổi, bạn có thể thêm logic xử lý nếu cần
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                // Không cần xử lý khi tin nhắn bị xóa, bạn có thể thêm logic xử lý nếu cần
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                // Không cần xử lý di chuyển tin nhắn trong trường hợp này
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy tin nhắn: " + error.getMessage());
            }
        });
    }

    // Lấy thông tin người dùng nhanh chóng và cập nhật vào tin nhắn
    private void fetchUserDetails(Message message, MessageListener listener) {
        usersRef.child(message.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);

                    // Cập nhật thông tin người dùng vào tin nhắn
                    message.setUsername(username != null ? username : "Unknown");
                    message.setAvatarUrl(avatarUrl != null ? avatarUrl : "");
                    message.setStatus(status != null ? status : "Offline");
                }

                Log.i("MessageController", "Tin nhắn mới: " + message.getContent() + " - " + message.getUsername());

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
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy thông tin người dùng: " + error.getMessage());
            }
        });
    }
}
