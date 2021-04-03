package com.endimuceku.groupup;

public class GroupChatMessage {

    private String message, sender, timestamp;

    public GroupChatMessage(String message, String sender, String timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public GroupChatMessage() {}

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
