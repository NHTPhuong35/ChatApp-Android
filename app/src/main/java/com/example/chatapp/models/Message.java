package com.example.chatapp.models;

public class Message {
    private String messageId, conversationId;
    private String senderId, content, createdAt; //createdAt: Thời gian gửi tin nhắn
    private boolean isRead; //Tin nhắn đã được đọc hay chưa (TRUE/FALSE)

    public Message() {}

    public Message(String messageId, String conversationId, String senderId, String content, String createdAt, boolean isRead) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
}
