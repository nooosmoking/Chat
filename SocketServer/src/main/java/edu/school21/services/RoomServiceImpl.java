package edu.school21.services;

import edu.school21.models.Chatroom;
import edu.school21.models.User;
import edu.school21.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("roomService")
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public List<Chatroom> findAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Chatroom> findRoomInList(List<Chatroom> rooms, String name) {
        return rooms.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    @Transactional
    public boolean createRoom(String name, User user, List<Chatroom> rooms) {
        Optional<Chatroom> room = findRoomInList(rooms, name);
        if (!room.isPresent()){
            Chatroom newRoom = new Chatroom(name, new LinkedList<>(Collections.singletonList(user)));
            rooms.add(newRoom);
            roomRepository.save(newRoom);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public Optional<Chatroom> chooseRoom(int roomIndex, User user, List<Chatroom> rooms) {
        Optional<Chatroom> room;
        try{
            room = Optional.of(rooms.get(roomIndex));
            room.get().getUserList().add(user);
        } catch (IndexOutOfBoundsException ex){
            room = Optional.empty();
        }
        return room;
    }
}
