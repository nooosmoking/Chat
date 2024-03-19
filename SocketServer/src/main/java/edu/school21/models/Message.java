package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private User sender;
    @JsonProperty("text")
    private String text;
    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
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

    public Message(String json, User sender, Chatroom room) throws JsonProcessingException, NoSuchElementException {
        ObjectMapper mapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        Message message = mapper.readValue(json, Message.class);
            this.text = message.getText();
            this.sender = sender;
            this.time = message.getTime();
            this.room = room;
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        return mapper.writeValueAsString(this);
    }
}
