package edu.school21.repositories;

import edu.school21.models.Message;

import java.util.List;
import java.util.Queue;

public interface MessageRepository extends CrudRepository<Message>{
    List<Message> findLastCountMessages(int count, String roomName);
}
