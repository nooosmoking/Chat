package edu.school21.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Queue;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chatroom {
    private String name;
    private List<User> userList;

    public User findUserByName(String login) {
        for (User user : userList
        ) {
            if (user.getLogin().equalsIgnoreCase(login)) {
                return user;
            }
        }
        return null;
    }
}
