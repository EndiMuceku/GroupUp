package com.endimuceku.groupup;

import java.util.HashMap;
import java.util.Map;

public class EventGroup {

    private String eventTitle;
    private String eventDescription;
    private String eventDate;
    private String eventTime;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postcode;
    private String location;
    private String eventType;
    private String creator;

    private Map<String, String> users = new HashMap<>();

    public EventGroup(String eventTitle, String eventDescription, String eventDate, String eventTime, String addressLine1, String addressLine2,
                      String addressLine3, String postcode, String location, String eventType, String creator) {

        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.postcode = postcode;
        this.location = location;
        this.eventType = eventType;
        this.creator = creator;

    }

    public EventGroup() {}

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getLocation() {
        return location;
    }

    public String getEventType() {
        return eventType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public boolean isCreator(String userID) {
        if (userID.equals(creator)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMember(String userID) {
        if(users.containsKey(userID)){
           return true;
        } else {
            return false;
        }
    }

    public void addUser(String userID, String userEmail){
        users.put(userID, userEmail);
    }

    public void removeUser(String userID) {
        users.remove(userID);
    }

}