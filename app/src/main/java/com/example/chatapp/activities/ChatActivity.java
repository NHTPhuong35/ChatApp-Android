package com.example.chatapp.activities;

import static com.example.chatapp.utils.Constants.MESSAGE_TYPE_TEXT;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.MessageAdapter;
import com.example.chatapp.controllers.MessageController;
import com.example.chatapp.models.Message;
import com.example.chatapp.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements MessageController.MessageListener {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private String currentUserId ; // ID người dùng hiện tại

    private MessageController messageController;
    private EditText editTextMultiLine;
    private ImageView btnSend, btnAttach, btnLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMultiLine = findViewById(R.id.editTextMultiLine);
        btnSend = findViewById(R.id.btn_send);
        btnAttach = findViewById(R.id.btn_attach);
        btnLike = findViewById(R.id.btn_like);

        messageList = new ArrayList<>();
        messageController = new MessageController();
        currentUserId = getCurrentUserId();

        // Cấu hình Adapter và RecyclerView
        adapter = new MessageAdapter(this, messageList,currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /// Lấy danh sách tin nhắn và lắng nghe dữ liệu
        messageController.fetchMessagesWithUserData(this);

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMultiLine.getText().toString();
            if (!messageText.isEmpty()) {
                messageController.sendMessage(currentUserId, messageText, MESSAGE_TYPE_TEXT, null);
                editTextMultiLine.setText(""); // Xoá nội dung sau khi gửi
            }
        });

    }

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid(); // Trả về UID của user
        } else {
            System.err.println("Lỗi: Không có user nào đang đăng nhập.");
            return null;
        }
    }

//    //hiển thị tin nhắn
//    @Override
//    public void onMessagesReceived(List<Message> messages) {
//        messageList.clear();
//        messageList.addAll(messages);
//        adapter.notifyDataSetChanged();
//        recyclerView.post(() -> recyclerView.smoothScrollToPosition(messageList.size() - 1));
//    }

    @Override
    public void onMessageAdded(Message message) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1); // Cuộn xuống tin nhắn mới nhất
    }
}
