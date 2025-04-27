package com.example.chatapp.activities;

import static com.example.chatapp.utils.Constants.MESSAGE_TYPE_ICON;
import static com.example.chatapp.utils.Constants.MESSAGE_TYPE_IMAGE;
import static com.example.chatapp.utils.Constants.MESSAGE_TYPE_TEXT;


import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.webauthn.Cbor;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.chatapp.R;
import com.example.chatapp.adapters.MessageAdapter;
import com.example.chatapp.controllers.MessageController;
import com.example.chatapp.models.Message;
import com.example.chatapp.websocket.WebSocketManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements MessageController.MessageListener,  WebSocketManager.AppWebSocketListener {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private String currentUserId ; // ID người dùng hiện tại

    private MessageController messageController;
    private EditText editTextMultiLine;
    private ImageView btnSend, btnAttach, btnLike, iconThreeDots ;

    //Gửi file hình ảnh
    private static final String TAG = "ChatActivity";
    private Uri selectedImageUri;

    WebSocketManager webSocketManager;
    private boolean isInitialLoading = true;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    uploadPhotoAndSendImageUrl(uri);
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        //kết nối Amplify
        configureAmplify();

        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMultiLine = findViewById(R.id.editTextMultiLine);
        btnSend = findViewById(R.id.btn_send);
        btnAttach = findViewById(R.id.btn_attach);
        btnLike = findViewById(R.id.btn_like);
        iconThreeDots = findViewById(R.id.icon_menu);

        messageList = new ArrayList<>();
        messageController = new MessageController();
        currentUserId = getCurrentUserId();

        // Cấu hình Adapter và RecyclerView
        adapter = new MessageAdapter(this, messageList,currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /// Lấy danh sách tin nhắn và lắng nghe dữ liệu
        messageController.fetchMessagesWithUserData(this);

        // Khởi tạo WebSocket
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.setListener(this); // ChatActivity lắng nghe WebSocket


        // Thiết lập Toolbar làm ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Thiết lập sự kiện khi người dùng click vào dấu ba chấm
        iconThreeDots.setOnClickListener(view -> showPopupMenu(view));

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMultiLine.getText().toString();
            if (!messageText.isEmpty()) {
                // Gửi tin nhắn qua WebSocket
                Message message = new Message(
                        null, // server tự gán messageId
                        currentUserId,
                        messageText,
                        MESSAGE_TYPE_TEXT,
                        System.currentTimeMillis(),
                        false
                );
                webSocketManager.sendMessage(message);
                messageController.sendMessage(currentUserId,  messageText, MESSAGE_TYPE_TEXT);
                editTextMultiLine.setText(""); // Xoá nội dung sau khi gửi

            }
        });

        btnAttach.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gửi tin nhắn với icon like khi nhấn nút like
                sendLikeMessage();
            }
        });
    }

    private void sendLikeMessage() {
        // Tạo đối tượng tin nhắn với nội dung là một biểu tượng like
        String likeIcon = "👍"; // hoặc bạn có thể sử dụng mã Unicode hoặc URL ảnh biểu tượng

        // Tạo tin nhắn với type là "like"
        Message message = new Message(
                null,
                currentUserId,
                likeIcon,
                MESSAGE_TYPE_ICON,
                System.currentTimeMillis(),
                false
        );

        webSocketManager.sendMessage(message);
        messageController.sendMessage(message.getSenderId(),message.getContent(), message.getType());
    }


    //Kết nối amplify
    private void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "Amplify configured successfully");
        } catch (AmplifyException e) {
            Log.e(TAG, "Could not initialize Amplify", e);
        }
    }

    private void uploadPhotoAndSendImageUrl(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "InputStream is null");
                return;
            }

            String photoKey = "chat_images/photo_" + System.currentTimeMillis() + ".jpg"; // nhớ có folder nếu S3 phân cấp
            StorageUploadInputStreamOptions options = StorageUploadInputStreamOptions.builder()
                    .contentType("image/jpeg")
                    .build();

            Amplify.Storage.uploadInputStream(
                    photoKey,
                    inputStream,
                    options,
                    result -> {
                        Log.i(TAG, "Upload succeeded: " + result.getKey());

                        // Nếu bucket là public, bạn tạo URL trực tiếp:
                        String imageUrl = "https://chatappandroidfcafd8d3b6f746b396e66fdb2c8c81ed95d52-dev.s3.us-east-1.amazonaws.com/public/" + result.getKey();

                        // Gửi tin nhắn chứa URL ảnh
                        runOnUiThread(() -> {

                            // Gửi URL ảnh qua WebSocket
                            Message message = new Message(
                                    null,
                                    currentUserId,
                                    imageUrl,
                                    MESSAGE_TYPE_IMAGE,
                                    System.currentTimeMillis(),
                                    false
                            );
                            webSocketManager.sendMessage(message);

                            messageController.sendMessage(currentUserId, imageUrl, MESSAGE_TYPE_IMAGE);
                        });
                    },
                    error -> Log.e(TAG, "Upload failed", error)
            );
        } catch (Exception e) {
            Log.e(TAG, "Exception opening stream", e);
        }
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

    @Override
    public void onMessageAdded(Message message) {
        messageList.add(message);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1); // Cuộn xuống tin nhắn mới nhất
    }


    public void onAllMessagesLoaded() {
        // Sau khi load hết từ Firebase thì set cờ
        isInitialLoading = false;

        if (messageList.size() > 0) {
            recyclerView.scrollToPosition(messageList.size() - 1);
        }
    }

    // Khi nhận tin nhắn mới từ WebSocket
    @Override
    public void onWebSocketMessageReceived(Message message) {
        runOnUiThread(() -> {
            if (isInitialLoading) return; // Đang load tin cũ thì bỏ qua
            if (message != null) {
                messageList.add(message);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    ///
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    // Tạo PopupMenu khi người dùng click vào dấu ba chấm
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.item_profile) {
                    // Chuyển đến màn hình Profile
                    Intent profileIntent = new Intent(ChatActivity.this, Profile.class);
                    startActivity(profileIntent);
                    return true;
                } else if (item.getItemId() == R.id.item_logout) {
                    // Đăng xuất người dùng
                    logoutUser();
                    return true;
                }
                return false;
            }
        });
    }

    // Xử lý đăng xuất
    private void logoutUser() {
        // Thêm chức năng đăng xuất (xóa dữ liệu người dùng, đăng xuất khỏi Firebase, v.v.)
        Intent logoutIntent = new Intent(ChatActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
        finish();
    }
}
