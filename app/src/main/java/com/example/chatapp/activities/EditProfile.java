package com.example.chatapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.controllers.UserController;
import com.example.chatapp.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.InputStream;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView imgAvatar;
    private TextView txtUsername, txtTag;
    private EditText edtDisplayName, edtEmail, edtPassword;
    private MaterialButton btnSave;
    private ImageButton btnEditAvatar;
    private Uri selectedAvatarUri;

    private UserController userController;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<String> avatarPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedAvatarUri = uri;
                    uploadAvatarAndUpdateProfile(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ánh xạ view
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        txtUsername = findViewById(R.id.txtUsername);
        txtTag = findViewById(R.id.txtTag);
        edtDisplayName = findViewById(R.id.edtDisplayName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);

        userController = new UserController();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Kết nối với Amplify
        configureAmplify();

        btnEditAvatar.setOnClickListener(v -> {
            avatarPickerLauncher.launch("image/*"); // Mở trình chọn ảnh khi nhấn nút edit
        });

        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Load thông tin người dùng từ Firebase Realtime Database
            userController.getUserInfo(userId, new UserController.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        // Hiển thị ảnh đại diện nếu có
                        Glide.with(EditProfile.this)
                                .load(user.getAvatarUrl())
                                .circleCrop()
                                .into(imgAvatar);
                    }

                    // Hiển thị tên người dùng và email trong các TextView
                    txtUsername.setText(user.getUsername() != null ? user.getUsername() : "Unknown");
                    txtTag.setText(user.getEmail() != null ? user.getEmail() : "No email");

                    // Điền các giá trị vào các EditText để người dùng có thể chỉnh sửa
                    edtDisplayName.setText(user.getUsername());
                    edtEmail.setText(user.getEmail());
                    edtPassword.setText(""); // Đảm bảo không hiển thị mật khẩu
                }

                @Override
                public void onFailure(Exception e) {
                    // Xử lý lỗi nếu không lấy được thông tin người dùng
                    Toast.makeText(EditProfile.this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Lắng nghe sự kiện nhấn nút lưu
        btnSave.setOnClickListener(view -> {
            saveProfile();
        });
    }

    // Kết nối Amplify để sử dụng S3
    private void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "Amplify configured successfully");
        } catch (AmplifyException e) {
            Log.e(TAG, "Could not initialize Amplify", e);
        }
    }

    private void uploadAvatarAndUpdateProfile(Uri avatarUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(avatarUri);
            if (inputStream == null) {
                Log.e(TAG, "InputStream is null");
                return;
            }

            String avatarKey = "avatars/avatar_" + System.currentTimeMillis() + ".jpg";
            StorageUploadInputStreamOptions options = StorageUploadInputStreamOptions.builder()
                    .contentType("image/jpeg")
                    .build();

            Amplify.Storage.uploadInputStream(
                    avatarKey,
                    inputStream,
                    options,
                    result -> {
                        Log.i(TAG, "Upload succeeded: " + result.getKey());

                        // Tạo URL ảnh từ key
                        String avatarUrl = "https://chatappandroidfcafd8d3b6f746b396e66fdb2c8c81ed95d52-dev.s3.us-east-1.amazonaws.com/public/" + result.getKey();

                        // Cập nhật URL ảnh đại diện vào Firebase Authentication
                        updateProfileAvatarInFirebase(avatarUrl);

                        // Cập nhật URL ảnh vào Firebase Realtime Database (Cần lưu lại URL vào Database)
                        updateAvatarInDatabase(avatarUrl);

                        // Cập nhật ảnh đại diện ngay trong giao diện
                        runOnUiThread(() -> Glide.with(EditProfile.this)
                                .load(avatarUrl)
                                .circleCrop()
                                .into(imgAvatar));

                    },
                    error -> Log.e(TAG, "Upload failed", error)
            );
        } catch (Exception e) {
            Log.e(TAG, "Exception opening stream", e);
        }
    }

    // Cập nhật URL ảnh vào Firebase Realtime Database
    private void updateAvatarInDatabase(String avatarUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Cập nhật avatarUrl trong Firebase Realtime Database
            userController.updateAvatar(userId, avatarUrl, new UserController.UpdateCallback() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "Avatar URL updated in Firebase Realtime Database");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to update avatar URL in Firebase DB", e);
                }
            });
        }
    }

    // Cập nhật ảnh đại diện trong Firebase
    private void updateProfileAvatarInFirebase(String avatarUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Cập nhật ảnh đại diện vào Firebase
            currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(avatarUrl))
                            .build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Profile updated with new avatar");
                        } else {
                            Log.e(TAG, "Profile update failed", task.getException());
                        }
                    });
        }
    }

    private void saveProfile() {
        String newDisplayName = edtDisplayName.getText().toString().trim();
        String newEmail = edtEmail.getText().toString().trim();
        String newPassword = edtPassword.getText().toString().trim();

        // Kiểm tra xem tất cả thông tin có được nhập đầy đủ không
        if (TextUtils.isEmpty(newDisplayName) || TextUtils.isEmpty(newEmail)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin người dùng
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Nếu mật khẩu mới được nhập, thì thực hiện cập nhật mật khẩu trong Firebase Authentication
            if (newPassword != null && !newPassword.isEmpty()) {
                currentUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sau khi cập nhật mật khẩu trong Firebase Authentication, cập nhật thông tin trong Firebase Realtime Database
                        updateUserInDatabase(userId, newDisplayName, newEmail, newPassword);
                    } else {
                        Toast.makeText(EditProfile.this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Nếu không có mật khẩu mới, chỉ cập nhật email và tên người dùng trong Firebase Realtime Database
                updateUserInDatabase(userId, newDisplayName, newEmail, null);
            }
        }
    }

    private void updateUserInDatabase(String userId, String newDisplayName, String newEmail, String newPassword) {
        userController.updateUser(userId, newDisplayName, newEmail, newPassword, new UserController.UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity, quay lại màn hình trước
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfile.this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
