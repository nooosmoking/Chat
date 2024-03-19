package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.school21.services.RoomService;
import edu.school21.services.UsersService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private User sender;
    @JsonProperty("text")
    private String text;
    private LocalDateTime time;
    private Chatroom room;

    @JsonProperty("sender_login")
    private String getSenderLogin() {
        return sender.getLogin();
    }

    @JsonProperty("chatroom_name")
    private String getChatroomName() {
        return room.getName();
    }

    @JsonProperty("time")
    private String getTime() {
        return time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public Message(String json, User sender, Chatroom room) throws JsonProcessingException, NoSuchElementException {
        ObjectMapper mapper = new ObjectMapper();
            Message message = mapper.readValue(json, Message.class);
            this.text = message.getText();
            this.sender = sender;
            this.time = LocalDateTime.parse(message.getTime(), DateTimeFormatter.ofPattern("dd.MM HH:mm"));
            this.room = room;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        return mapper.writeValueAsString(this);
    }
}
