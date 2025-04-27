package com.example.chatapp.models;

public class User {
    private String userId;
    private String email;
    private String password;
    private String username;
    private String avatarUrl;
    private String status; // online, offline, busy
    private String createdAt;

    public User() {}

    public User(String userId, String email, String password, String username, String avatarUrl, String status, String createdAt) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public User(String userId, String email, String password,String status,  String createdAt){
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
