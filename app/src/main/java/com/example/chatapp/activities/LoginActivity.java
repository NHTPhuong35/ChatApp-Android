package com.example.chatapp.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.chatapp.R;
import com.example.chatapp.controllers.UserController;
import com.example.chatapp.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class LoginActivity extends AppCompatActivity {
    private UserController userController;
    private TextView registerText;
    private TextView snackbarText;
    private ImageView snackbarIcon;
    private Button loginButton;
    private TextInputLayout emailTextLayout;
    private TextInputLayout passwordTextLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkDarkMode(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                Trở về màn hình chính của điện thoại
                finishAffinity();
            }
        });

        View rootLayout = findViewById(R.id.login);
        rootLayout.setOnTouchListener((v, event) -> {
//            Ẩn bàn phím và xóa focus khi chạm vào vùng trống
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard();
            }
//            Không chặn sự kiện, để các view khác hoạt động bình thường
            return false;
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
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        });
    }

    private void loginUser() {
//        String email = "phuong123@gmail.com";
//        String password = "Phuong123@";
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());

//        Đăng nhập tài khoản
        if (checkInput(email, password)) {
            userController = new UserController();
            userController.loginUser(email, password, new UserController.LoginCallback() {
                @Override
                public void onSuccess(User user) {
                    showCustomSnackbar(
                            findViewById(android.R.id.content),
                            "Đăng nhập thành công!",
                            R.drawable.ic_check,
                            getResources().getColor(R.color.green)
                    );
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    startActivity(intent);

                    Log.d("Login", "Đăng nhập thành công!");
                    Log.d("Login", user.getUserId() + " - " + user.getEmail() + " - " + user.getUsername() + " - " + user.getAvatarUrl());
                }

                @Override
                public void onFailure(Exception e) {
                    showCustomSnackbar(
                            findViewById(android.R.id.content),
                            "Đăng nhập thất bại!",
                            R.drawable.ic_error,
                            getResources().getColor(R.color.red)
                    );

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

//        Kiểm tra rỗng
        if (email.isBlank()) {
            emailTextLayout.setError("* Vui lòng nhập email");
            emailEditText.requestFocus();
            errors[0] = true;
        } else {
            emailTextLayout.setError(null);
        }
        if (password.isBlank()) {
            passwordTextLayout.setError("* Vui lòng nhập mật khẩu");
            passwordEditText.requestFocus();
            errors[1] = true;
        } else {
            passwordTextLayout.setError(null);
        }

//        Kiểm tra cú pháp email
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            emailTextLayout.setError("* Vui lòng nhập đúng định dạng email");
            emailEditText.requestFocus();
            errors[0] = true;
        } else {
            emailTextLayout.setError(null);
            errors[0] = false;
        }

//        Kiểm tra độ dài mật khẩu
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
    
    private void checkDarkMode(Context context) {
        ImageView loginIV = findViewById(R.id.image_login);

        boolean isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        Drawable bgLogin = ContextCompat.getDrawable(this, isDarkMode ? R.drawable.bg_login_dark : R.drawable.bg_login);

        loginIV.setImageDrawable(bgLogin);
    }

    private void clearFocusAndHideKeyboard() {
//        Lấy View đang focus
        View currentFocus = getCurrentFocus();

        if (currentFocus instanceof TextInputEditText) {
//            Xóa focus khỏi TextInputEditText
            currentFocus.clearFocus();

//            Ẩn bàn phím
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    @SuppressLint("RestrictedApi")
    public void showCustomSnackbar(View parentView, String textSnackbar, @DrawableRes int resId, @ColorInt int colorId) {
        // Tạo Snackbar
        Snackbar snackbar = Snackbar.make(parentView, "", Snackbar.LENGTH_INDEFINITE);

        // Lấy view của Snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0); // Loại bỏ padding mặc định

        // Inflate layout tùy chỉnh
        LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
        View customView = inflater.inflate(R.layout.custom_snackbar, null);
        snackbarText = customView.findViewById(R.id.snackbar_text);
        snackbarText.setText(textSnackbar);
        snackbarIcon = customView.findViewById(R.id.snackbar_icon);
        snackbarIcon.setImageResource(resId);

        // Thêm custom view vào snackbar
        snackbarLayout.addView(customView, 0);

        // Căn chỉnh vị trí của Snackbar ở cuối màn hình
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarLayout.setLayoutParams(params);

        // Hiển thị Snackbar
        snackbar.show();

        // Lấy ProgressBar từ layout
        ProgressBar progressBar = customView.findViewById(R.id.snackbar_progress);
        progressBar.setProgressTintList(ColorStateList.valueOf(colorId));

        // Thiết lập ProgressBar chạy trong 3 giây
        int duration = 3000;
        ValueAnimator animator = ValueAnimator.ofInt(0, duration);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            progressBar.setProgress(progress);
        });
        animator.start();

        // Handler để ẩn Snackbar sau 3 giây với hiệu ứng trượt ra ngoài
        new Handler().postDelayed(() -> {
            snackbar.getView().animate()
                    .translationX(-snackbar.getView().getWidth()) // Trượt sang trái
                    .setDuration(300) // Thời gian trượt
                    .withEndAction(snackbar::dismiss) // Xóa Snackbar khi trượt xong
                    .start();
        }, duration);
    }
}
