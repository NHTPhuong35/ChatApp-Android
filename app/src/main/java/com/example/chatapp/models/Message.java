package com.example.chatapp.models;

import com.google.firebase.Timestamp;

public class Message {

    private String messageId, senderId, content;
    private String type;
    private Long createdAt; // Thời gian tạo tin nhắn
    private boolean isRead; // Tin nhắn đã đọc hay chưa

    private String username;
    private String avatarUrl;
    private String status;

    // Constructor mặc định cho Firebase
    public Message() {}

    public Message(String messageId, String senderId, String content, String type,
                   Long createdAt, boolean isRead, String username, String avatarUrl, String status) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.status = status;
    }


    // Constructor đầy đủ
    public Message(String messageId, String senderId, String content, String type,
                   Long createdAt, boolean isRead, String username, String avatarUrl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }


    public Message(String messageId, String senderId, String content, String type,
                   Long createdAt, boolean isRead) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Getters và Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }

    public String getAvatarUrl(){ return avatarUrl; }
    public void setAvatarUrl(String avatarUrl){ this.avatarUrl = avatarUrl; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
}
