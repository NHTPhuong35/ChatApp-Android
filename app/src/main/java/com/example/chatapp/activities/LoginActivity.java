package com.example.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.controllers.UserController;
import com.example.chatapp.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class LoginActivity extends AppCompatActivity {
    private UserController userController;
    private TextView registerText;
    private Button loginButton;
    private TextInputLayout emailTextLayout;
    private TextInputLayout passwordTextLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View rootLayout = findViewById(R.id.login); // Thay R.id.root_layout bằng ID của layout gốc

        rootLayout.setOnTouchListener((v, event) -> {
            // Ẩn bàn phím và xóa focus khi chạm vào vùng trống
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard();
            }
            return false; // Không chặn sự kiện, để các view khác hoạt động bình thường
        });

        loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        registerText = findViewById(R.id.text_register_2);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {
//        String email = "phuong123@gmail.com";
//        String password = "Phuong123@";
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());

        // Đăng nhập tài khoản
        if (checkInput(email, password)) {
            userController = new UserController();
            userController.loginUser(email, password, new UserController.LoginCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.d("Login", "Đăng nhập thành công!");
                    Log.d("Login", user.getUserId() + " - " + user.getEmail() + " - " + user.getUsername() + " - " + user.getAvatarUrl());
                    // Chuyển sang màn hình chính
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("Register", "Lỗi: " + e.getMessage());
                }
            });
        }
    }

    private boolean checkInput(String email, String password) {
        emailTextLayout = findViewById(R.id.text_input_layout_email);
        passwordTextLayout = findViewById(R.id.text_input_layout_password);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);

        boolean[] errors = {false, false};

        // Kiểm tra rỗng
        if (email.isBlank()) {
            emailTextLayout.setError("* Vui lòng nhập email");
            emailEditText.requestFocus();
            errors[0] = true;
        } else {
            emailTextLayout.setError(null);
            errors[0] = false;
        }
        if (password.isBlank()) {
            passwordTextLayout.setError("* Vui lòng nhập mật khẩu");
            passwordEditText.requestFocus();
            errors[1] = true;
        } else {
            passwordTextLayout.setError(null);
            errors[1] = false;
        }

        // Kiểm tra cú pháp email
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            emailTextLayout.setError("* Vui lòng nhập đúng định dạng email");
            emailEditText.requestFocus();
            errors[0] = true;
        } else {
            emailTextLayout.setError(null);
            errors[0] = false;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            passwordTextLayout.setError("* Mật khẩu phải có ít nhất 6 ký tự");
            passwordEditText.requestFocus();
            errors[1] = true;
        } else if (password.length() > 50) {
            passwordTextLayout.setError("* Mật khẩu không được quá 50 ký tự");
            passwordEditText.requestFocus();
            errors[1] = true;
        } else {
            passwordTextLayout.setError(null);
            errors[1] = false;
        }

        return !errors[0] && !errors[1];
    }

    private void clearFocusAndHideKeyboard() {
        // Lấy View đang focus
        View currentFocus = getCurrentFocus();

        if (currentFocus instanceof TextInputEditText) {
            // Xóa focus khỏi TextInputEditText
            currentFocus.clearFocus();

            // Ẩn bàn phím
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}
