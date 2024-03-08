package edu.school21.services;

import edu.school21.models.Chatroom;
import edu.school21.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public boolean createRoom(Chatroom room) {
        //
        return true;
    }
}
