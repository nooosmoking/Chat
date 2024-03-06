package edu.school21.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chatroom {
    private long id;
    private String name;
    private List<User> userList;

    public User findUserByName(String login){
        for (User user:userList
             ) {
            if (user.getLogin().equals(login)){
                return user;
            }
        }
        return null;
    }
}
