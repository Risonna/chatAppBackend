package com.chatapp.webchat.entities;

public class ChatRoom {
    private int chatRoomId;
    private String name;
    private String createdAt; // Assuming you want this as a String, adjust if needed
    private String updatedAt; // Assuming you want this as a String, adjust if needed

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Other chat room-related fields, getters, and setters
}
