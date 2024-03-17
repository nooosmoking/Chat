package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("text")
    private String text;
    private LocalDateTime time;
    private Chatroom room;

    @JsonProperty("sender_login")
    private String getSenderLogin() {
        return sender.getLogin();
    }

    @JsonProperty("time")
    private String getTime() {
        return time.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
    }

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");

        return time.format(formatter) + " " + sender.getLogin() + "\n" + text;
    }
}
