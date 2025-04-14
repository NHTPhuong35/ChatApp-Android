package com.example.chatapp.models;

import com.google.firebase.Timestamp;

public class Message {

    private String messageId, senderId, content, attachmentUrl;
    private String type;
    private Long createdAt; // Thời gian tạo tin nhắn
    private boolean isRead; // Tin nhắn đã đọc hay chưa

    private String userName;
    private String avatarUrl;
    private String status;

    // Constructor mặc định cho Firebase
    public Message() {}

    // Constructor đầy đủ
    public Message(String messageId, String senderId, String content, String type,
                   String attachmentUrl, Long createdAt, boolean isRead,
                   String userName, String avatarUrl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.attachmentUrl = attachmentUrl;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }


    public Message(String messageId, String senderId, String content, String type,
                   String attachmentUrl, Long createdAt, boolean isRead) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.attachmentUrl = attachmentUrl;
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

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public String getUserName(){ return userName; }
    public void setUserName(String userName){ this.userName = userName; }

    public String getAvatarUrl(){ return avatarUrl; }
    public void setAvatarUrl(String avatarUrl){ this.avatarUrl = avatarUrl; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
}
