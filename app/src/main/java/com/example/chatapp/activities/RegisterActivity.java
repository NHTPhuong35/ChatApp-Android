package com.example.chatapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.controllers.UserController;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userController = new UserController();

        String email = "phuong123@gmail.com";
        String password = "Phuong123@";
        String username = "Phuong";
        String avatarURL = "img.png";

        // Đăng ký tài khoản
        userController.registerUser(email, password, new UserController.RegisterCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                // Cập nhật thông tin người dùng
                userController.updateUsername(user.getUid(), username);
                userController.updateAvatar(user.getUid(), avatarURL);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(RegisterActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
