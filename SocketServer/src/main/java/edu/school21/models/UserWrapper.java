package edu.school21.models;

import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Getter
@Setter
public class UserWrapper {
    private User user;

    public UserWrapper(DataOutputStream out, DataInputStream in){
        user = new User(null, null, out, in, false);
    }
}