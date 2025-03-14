package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.controllers.UserController;
import com.example.chatapp.models.User;

public class LoginActivity extends AppCompatActivity {
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userController = new UserController();

        String email = "phuong123@gmail.com";
        String password = "Phuong123@";

        userController.loginUser(email, password, new UserController.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, user.getUserId() + " - " + user.getEmail() + " - " + user.getUsername() + " - " + user.getAvatarUrl() + " -", Toast.LENGTH_SHORT).show();
                // Chuyển sang màn hình chính

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
