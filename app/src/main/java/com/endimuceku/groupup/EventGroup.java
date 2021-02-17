package com.endimuceku.groupup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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

    private Map<String, String> userMap;

    public EventGroup(String eventTitle, String eventDescription, String eventDate, String eventTime, String addressLine1, String addressLine2,
                      String addressLine3, String postcode, String location, String eventType, Map<String, String> map) {
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

        userMap = map;

    }

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

    public boolean isOwner(String userEmail) {
        if (userEmail.equals(userMap.values().toArray()[0])) {
            return true;
        } else {
            return false;
        }
    }

}
