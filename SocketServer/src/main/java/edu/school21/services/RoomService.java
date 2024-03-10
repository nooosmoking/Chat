package edu.school21.services;

import edu.school21.models.Chatroom;
import edu.school21.models.User;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Chatroom> findAllRooms();
    Optional<Chatroom> findRoomInList(List<Chatroom> rooms, String name);
    boolean createRoom(String name, User user, List<Chatroom> rooms);
    boolean chooseRoom(String name, User user, List<Chatroom> rooms);
}
