package com.example.chatapp.models;

public class Conversation {
    private String conversationId, conversationName, createdAt;
    private boolean isGroup;

    public Conversation() {}

    public Conversation(String conversationId, String conversationName, boolean isGroup, String createdAt) {
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.isGroup = isGroup;
        this.createdAt = createdAt;
    }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getConversationName() { return conversationName; }
    public void setConversationName(String conversationName) { this.conversationName = conversationName; }

    public boolean isGroup() { return isGroup; }
    public void setGroup(boolean group) { isGroup = group; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
