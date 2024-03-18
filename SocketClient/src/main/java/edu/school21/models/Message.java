package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Data
public class Message {
    @JsonProperty("sender_login")
    private String sender;
    @JsonProperty("text")
    private String text;
    private LocalDateTime time;
    @JsonProperty("chatroom_name")
    private String room;

    @JsonProperty("time")
    private String getTime() {
        return time.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
    }

    public Message(String json) throws JsonProcessingException, NoSuchElementException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(json, Message.class);
        this.text = message.getText();
        this.sender = message.getSender();
        this.time = LocalDateTime.parse(message.getTime(), DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        this.room = message.getRoom();
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");

        return time.format(formatter) + " " + sender + "\n" + text;
    }
}

