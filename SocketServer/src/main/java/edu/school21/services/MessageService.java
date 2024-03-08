package edu.school21.services;

import edu.school21.models.Message;

import java.util.List;

public interface MessageService {
    List<Message> findLastCountMessages(int count);

    boolean save(Message msg);
}
