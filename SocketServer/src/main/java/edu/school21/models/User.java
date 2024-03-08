package edu.school21.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User {
    private String login;
    private String password;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isActive;
}
