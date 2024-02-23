package edu.school21.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private long id;
    private User sender;
    private String text;
    private LocalDateTime time;

    @Override
    public String toString(){
        return sender+" "+text;
    }
}
