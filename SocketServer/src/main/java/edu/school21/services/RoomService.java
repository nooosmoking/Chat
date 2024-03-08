package edu.school21.services;

import edu.school21.models.Chatroom;
import edu.school21.models.User;

import java.util.List;

public interface RoomService {
    List<Chatroom> findAllRooms();
    Chatroom findRoomInList();
    boolean createRoom(String name, User user, List<Chatroom> rooms);
    boolean chooseRoom(User user, List<Chatroom> rooms);
}
