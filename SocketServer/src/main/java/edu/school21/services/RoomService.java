package edu.school21.services;

import edu.school21.models.Chatroom;

import java.util.List;

public interface RoomService {
    List<Chatroom> findAllRooms();
    boolean createRoom(Chatroom room);
}
