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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {
    private UserController userController;
    private TextView loginText;
    private Button registerButton;
    private TextInputLayout usernameTextLayout;
    private TextInputLayout emailTextLayout;
    private TextInputLayout passwordTextLayout;
    private TextInputLayout repasswordTextLayout;
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText repasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View rootLayout = findViewById(R.id.register); // Thay R.id.root_layout bằng ID của layout gốc
        rootLayout.setOnTouchListener((v, event) -> {
            // Ẩn bàn phím và xóa focus khi chạm vào vùng trống
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard();
            }
            return false; // Không chặn sự kiện, để các view khác hoạt động bình thường
        });

        registerButton = findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        loginText = findViewById(R.id.text_login_2);
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
//        String email = "phuong123@gmail.com";
//        String password = "Phuong123@";
//        String username = "Phuong";
//        String avatarURL = "img.png";
        usernameEditText = findViewById(R.id.edit_text_user);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        repasswordEditText = findViewById(R.id.edit_text_repassword);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        String username = String.valueOf(usernameEditText.getText());
        String repassword = String.valueOf(repasswordEditText.getText());
        String avatarURL = null;

        // Đăng ký tài khoản
        if (checkInput(username, email, password, repassword)) {
            userController = new UserController();
            userController.registerUser(email, password, new UserController.RegisterCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Log.d("Register", "Đăng ký thành công: " + user.getEmail());
                    // Cập nhật thông tin người dùng
                    userController.updateUsername(user.getUid(), username);
                    userController.updateAvatar(user.getUid(), avatarURL);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("Register", "Lỗi: " + errorMessage);
                }
            });
        }
    }

    private boolean checkInput(String username, String email, String password, String repassword) {
        usernameTextLayout = findViewById(R.id.text_input_layout_user);
        emailTextLayout = findViewById(R.id.text_input_layout_email);
        passwordTextLayout = findViewById(R.id.text_input_layout_password);
        repasswordTextLayout = findViewById(R.id.text_input_layout_repassword);

        usernameEditText = findViewById(R.id.edit_text_user);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        repasswordEditText = findViewById(R.id.edit_text_repassword);

        boolean errors[] = {false, false, false, false};

        // Kiểm tra rỗng
        if (username.isBlank()) {
            usernameTextLayout.setError("* Vui lòng nhập tên người dùng");
            usernameEditText.requestFocus();
            errors[0] = true;
        } else {
            usernameTextLayout.setError(null);
            errors[0] = false;
        }
        if (email.isBlank()) {
            emailTextLayout.setError("* Vui lòng nhập email");
            emailEditText.requestFocus();
            errors[1] = true;
        } else {
            emailTextLayout.setError(null);
            errors[1] = false;
        }
        if (password.isBlank()) {
            passwordTextLayout.setError("* Vui lòng nhập mật khẩu");
            passwordEditText.requestFocus();
            errors[2] = true;
        } else {
            passwordTextLayout.setError(null);
            errors[2] = false;
        }

        // Kiểm tra cú pháp email
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            emailTextLayout.setError("* Vui lòng nhập đúng định dạng email");
            emailEditText.requestFocus();
            errors[1] = true;
        } else {
            emailTextLayout.setError(null);
            errors[1] = false;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            passwordTextLayout.setError("* Mật khẩu phải có ít nhất 6 ký tự");
            passwordEditText.requestFocus();
            errors[2] = true;
        } else if (password.length() > 50) {
            passwordTextLayout.setError("* Mật khẩu không được quá 50 ký tự");
            passwordEditText.requestFocus();
            errors[2] = true;
        } else {
            passwordTextLayout.setError(null);
            errors[2] = false;
        }

        // Kiểm tra mật khẩu trùng khớp
        if (!password.equals(repassword)) {
            repasswordTextLayout.setError("* Mật khẩu không trùng khớp");
            repasswordEditText.requestFocus();
            errors[3] = true;
        } else {
            repasswordTextLayout.setError(null);
            errors[3] = false;
        }

        return !errors[0] && !errors[1] && !errors[2] && !errors[3];
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
