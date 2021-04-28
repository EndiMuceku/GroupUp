package com.endimuceku.groupup;

// Class for storing a group chat message
public class GroupChatMessage {

    private String message, sender, timestamp;

    public GroupChatMessage(String message, String sender, String timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public GroupChatMessage() {}

    // Getters
    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

}