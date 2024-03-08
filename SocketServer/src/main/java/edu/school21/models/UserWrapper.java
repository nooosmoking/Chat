package edu.school21.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class UserWrapper {
    private User user;

    public UserWrapper(DataOutputStream out, DataInputStream in){
        user = new User(null, null, out, in, false);
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}