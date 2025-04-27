package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.controllers.UserController;
import com.example.chatapp.models.User; // Bị thiếu import này
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView txtUsername, txtTag, createdDate;
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Ánh xạ các View trong layout
        imgAvatar = findViewById(R.id.imgAvatar);
        txtUsername = findViewById(R.id.txtUsername);
        txtTag = findViewById(R.id.txtTag);
        createdDate = findViewById(R.id.createdDate);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Lấy thông tin người dùng hiện tại từ FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnEditProfile.setOnClickListener(view ->{
            Intent editProfileIntent = new Intent(Profile.this, EditProfile.class);
            startActivity(editProfileIntent);
        });

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Lấy thông tin người dùng từ Firebase Realtime Database
            UserController userController = new UserController();
            userController.getUserInfo(userId, new UserController.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    // Cập nhật giao diện với thông tin người dùng từ Firebase
                    txtUsername.setText(user.getUsername() != null ? user.getUsername() : "Unknown");
                    txtTag.setText(user.getEmail() != null ? user.getEmail() : "No Email");

                    // Hiển thị ngày gia nhập (có thể là createdAt từ Firebase)
                    if (user.getCreatedAt() != null) {
                        createdDate.setText("📅 " + user.getCreatedAt());
                    }

                    // Cập nhật ảnh đại diện nếu có
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(Profile.this)
                                .load(user.getAvatarUrl())
                                .circleCrop()
                                .into(imgAvatar);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // Hiển thị lỗi khi không thể lấy thông tin người dùng
                    txtUsername.setText("Error fetching user data");
                }
            });
        }
    }
}
