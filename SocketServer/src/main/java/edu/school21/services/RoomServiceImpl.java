package edu.school21.services;

import edu.school21.models.Chatroom;
import edu.school21.models.User;
import edu.school21.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("roomService")
@Transactional
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }
    @Override
    public List<Chatroom> findAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Chatroom> findRoomInList(List<Chatroom> rooms, String name) {
        return rooms.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public boolean createRoom(String name, User user, List<Chatroom> rooms) {
        Optional<Chatroom> room = findRoomInList(rooms, name);
        if (!room.isPresent()){
            Chatroom newRoom = new Chatroom(name, new LinkedList<>(Collections.singletonList(user)));
            roomRepository.save(newRoom);
            rooms.add(newRoom);
            return true;
        } else {
            return false;
        }
    }

    @Override
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
