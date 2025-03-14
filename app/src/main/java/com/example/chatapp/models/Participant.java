package com.example.chatapp.models;

public class Participant {
    private String participantId, conversationId, userId, joinedAt;
    //joinedAt: Thời gian người dùng tham gia cuộc trò chuyện

    public Participant() {}

    public Participant(String participantId, String conversationId, String userId, String joinedAt) {
        this.participantId = participantId;
        this.conversationId = conversationId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getJoinedAt() { return joinedAt; }
    public void setJoinedAt(String joinedAt) { this.joinedAt = joinedAt; }
}
