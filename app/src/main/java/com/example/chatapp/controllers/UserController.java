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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseHelper", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                long creationTime = user.getMetadata().getCreationTimestamp();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                String creationDate = sdf.format(new Date(creationTime));

                                String userID = user.getUid();
                                User userData = new User(userID, email, password,"offline", creationDate);
                                databaseRef.child(userID).setValue(userData);

                                // Gọi callback để thông báo thành công
                                callback.onSuccess(user);
                            }
                        } else {
                            Log.w("FirebaseHelper", "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    // Cập nhật username
    public void updateUsername(String userID, String username) {
        databaseRef.child(userID).child("username").setValue(username);
    }

    // Cập nhật avatar
    public void updateAvatar(String userID, String avatarURL) {
        databaseRef.child(userID).child("avatar_url").setValue(avatarURL);
    }

    // Interface callback để xử lý phản hồi
    public interface RegisterCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public void loginUser(String email, String password, LoginCallback callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userID = firebaseUser.getUid();
                            databaseRef.child(userID).get().addOnCompleteListener(dataTask -> {
                                if (dataTask.isSuccessful() && dataTask.getResult().exists()) {
                                    // Lấy thông tin user từ Firebase
                                    String emailDB = dataTask.getResult().child("email").getValue(String.class);
                                    String passwordDB = dataTask.getResult().child("password").getValue(String.class);
                                    String usernameDB = dataTask.getResult().child("username").getValue(String.class);
                                    String avatarURL = dataTask.getResult().child("avatar_url").getValue(String.class);
                                    String status = dataTask.getResult().child("status").getValue(String.class);
                                    String creationDate = dataTask.getResult().child("creationDate").getValue(String.class);

                                    // Tạo đối tượng User
                                    User user = new User(userID, emailDB, passwordDB, usernameDB, avatarURL,status, creationDate);

                                    // Gọi callback thành công
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

}
