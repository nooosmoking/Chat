package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@AllArgsConstructor
@NoArgsConstructor
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("sender_login")
    private String sender;
    @JsonProperty("text")
    private String text;
    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime time;
    @JsonProperty("chatroom_name")
    private String room;

    public Message(String json) throws JsonProcessingException, NoSuchElementException {
        ObjectMapper mapper =
                new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Message message = mapper.readValue(json, Message.class);
        this.text = message.getText();
        this.sender = message.getSender();
        this.time = message.getTime();
        this.room = message.getRoom();
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        return time.format(formatter) + " " + sender + "\n|" + text;
    }
}

