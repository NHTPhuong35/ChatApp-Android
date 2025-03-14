package com.example.chatapp.models;

public class Attachment {
    private String attachmentId, messageId, fileUrl, fileType, uploadedAt;
    private int fileSize;

    public Attachment() {}

    public Attachment(String attachmentId, String messageId, String fileUrl, String fileType, int fileSize, String uploadedAt) {
        this.attachmentId = attachmentId;
        this.messageId = messageId;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

    public String getAttachmentId() { return attachmentId; }
    public void setAttachmentId(String attachmentId) { this.attachmentId = attachmentId; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public int getFileSize() { return fileSize; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }

    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
