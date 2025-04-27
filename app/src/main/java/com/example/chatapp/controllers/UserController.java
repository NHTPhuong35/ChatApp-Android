package com.example.chatapp.controllers;

import static com.example.chatapp.utils.Constants.USERS_NODE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserController {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    public UserController() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    // Đăng ký tài khoản Firebase
    public void registerUser(String email, String password, RegisterCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("UserController", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            long creationTime = user.getMetadata().getCreationTimestamp();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                            String creationDate = sdf.format(new Date(creationTime));

                            String userID = user.getUid();
                            User userData = new User(userID, email, password, "offline", creationDate);
                            databaseRef.child(userID).setValue(userData)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        }
                    } else {
                        Log.w("UserController", "createUserWithEmail:failure", task.getException());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Đăng nhập
    public void loginUser(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userID = firebaseUser.getUid();
                            databaseRef.child(userID).get().addOnCompleteListener(dataTask -> {
                                if (dataTask.isSuccessful() && dataTask.getResult().exists()) {
                                    String emailDB = dataTask.getResult().child("email").getValue(String.class);
                                    String passwordDB = dataTask.getResult().child("password").getValue(String.class);
                                    String usernameDB = dataTask.getResult().child("username").getValue(String.class);
                                    String avatarURL = dataTask.getResult().child("avatarUrl").getValue(String.class);
                                    String status = dataTask.getResult().child("status").getValue(String.class);
                                    String createdAt = dataTask.getResult().child("createdAt").getValue(String.class);

                                    User user = new User(userID, emailDB, passwordDB, usernameDB, avatarURL, status, createdAt);
                                    callback.onSuccess(user);
                                } else {
                                    callback.onFailure(new Exception("Không tìm thấy thông tin user."));
                                }
                            });
                        } else {
                            callback.onFailure(new Exception("User không tồn tại."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Cập nhật username
    public void updateUsername(String userID, String username) {
        databaseRef.child(userID).child("username").setValue(username);
    }

    // Cập nhật avatar
    public void updateAvatar(String userID, String avatarURL) {
        databaseRef.child(userID).child("avatarUrl").setValue(avatarURL);
    }

    // Cập nhật email và mật khẩu trong Firebase Authentication
    public void updateUserEmailAndPassword(String userId, String newEmail, String newPassword, final UpdateCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Cập nhật email trong Firebase Authentication
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Cập nhật mật khẩu trong Firebase Authentication
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(passwordTask -> {
                                        if (passwordTask.isSuccessful()) {
                                            // Nếu cập nhật thành công cả email và mật khẩu
                                            callback.onSuccess();
                                        } else {
                                            // Nếu cập nhật mật khẩu không thành công
                                            callback.onFailure(passwordTask.getException());
                                        }
                                    });
                        } else {
                            // Nếu cập nhật email không thành công
                            callback.onFailure(task.getException());
                        }
                    });
        } else {
            callback.onFailure(new Exception("User not logged in."));
        }
    }

    // Cập nhật avatar URL trong Firebase Realtime Database
    public void updateAvatar(String userID, String avatarURL, final UpdateCallback callback) {
        databaseRef.child(userID).child("avatarUrl").setValue(avatarURL)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    // Cập nhật thông tin người dùng
    public void updateUser(String userId, String newUsername, String newEmail, String newPassword, final UpdateCallback callback) {
        if (userId == null) {
            callback.onFailure(new Exception("User ID is null"));
            return;
        }

        // Cập nhật username và email
        databaseRef.child(userId).child("username").setValue(newUsername);
        databaseRef.child(userId).child("email").setValue(newEmail);

        // Chỉ cập nhật mật khẩu nếu có mật khẩu mới
        if (newPassword != null && !newPassword.isEmpty()) {
            // Cập nhật mật khẩu trong Firebase Realtime Database
            databaseRef.child(userId).child("password").setValue(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        // Sau khi cập nhật mật khẩu, cũng cập nhật mật khẩu trong Firebase Authentication
                        updateUserEmailAndPassword(userId, newEmail, newPassword, callback);
                    })
                    .addOnFailureListener(callback::onFailure);
        } else {
            // Nếu không có mật khẩu mới, chỉ cập nhật thông tin còn lại
            updateUserEmailAndPassword(userId, newEmail, newPassword, callback);
        }
    }


    // Lấy thông tin user
    public void getUserInfo(String userId, final UserCallback callback) {
        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);
                    String createdAt = dataSnapshot.child("createdAt").getValue(String.class);

                    User user = new User(userId, email, password, username, avatarUrl, status, createdAt);
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(new Exception("User not found in Firebase"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

    // Callback Interfaces
    public interface RegisterCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
