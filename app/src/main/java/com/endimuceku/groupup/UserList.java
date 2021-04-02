package com.endimuceku.groupup;

import java.util.HashMap;

public class UserList {

    private HashMap<String, String> users;

    public UserList() {
        users = new HashMap<>();
    }


    public void addUser (String userID, String displayName) {
        users.put(userID, displayName);
    }

    public void removeUser (String userID) {
        users.remove(userID);
    }

    public HashMap<String, String> getUserHashMap() {
        return users;
    }


}
