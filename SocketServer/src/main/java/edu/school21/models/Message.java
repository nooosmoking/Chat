package edu.school21.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private User sender;
    private String text;
    private LocalDateTime time;
    private Chatroom room;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        String formattedTime = time.format(formatter);

        String login = sender.getLogin();
        int spacesToAdd = 20 - login.length();

        StringBuilder sb = new StringBuilder().append(" ");

        for (int i = 0; i < spacesToAdd; i++) {
            sb.append(" ");
        }

        return login + sb + text + "   " + formattedTime;
    }
}
