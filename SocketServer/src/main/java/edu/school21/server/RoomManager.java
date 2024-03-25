package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.RoomService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class RoomManager {
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);
    private final List<Chatroom> roomList;
    private final RoomService roomService;
    private DataOutputStream out;
    private DataInputStream in;
    private final Map<Integer, Supplier<Boolean>> commandMap = new HashMap<>();
    @Getter
    private Chatroom currRoom;
    private User user;

    public RoomManager(List<Chatroom> roomList, RoomService roomService) {
        this.roomList = roomList;
        this.roomService= roomService;
        commandMap.put(1, this::createRoom);
        commandMap.put(2, this::chooseRoom);
    }

    public void setUser(UserWrapper userWrapper){
        this.user = userWrapper.getUser();
        this.out = user.getOut();
        this.in = user.getIn();
    }
    public boolean doRoomLogic() throws IOException {
        out.writeUTF("1. Create room\n" + "2. Choose room\n" + "3. Exit");
        out.flush();
        int answer;
        while (true) {
            try {
                answer = Integer.parseInt(in.readUTF());
                if (answer == 3) {
                    logger.info("Client " + user.getLogin() + " left chat");
                    return false;
                } else if (!commandMap.get(answer).get()) {
                    return false;
                }
                break;
            } catch (NullPointerException e) {
                out.writeUTF("Unknown command." + " Please try again.");
            }
        }
        out.writeUTF(currRoom.getName() + " (for exiting write \"exit\") \n---");
        out.flush();
        return true;
    }

    private boolean createRoom() {
        try {
            String roomName;
            while (true) {
                logger.info("Client " + user.getLogin() + " trying to create room");
                out.writeUTF("Enter a name of chatroom (for exiting write \"exit\"):");
                out.flush();
                roomName = in.readUTF();
                if (roomName.length() > 30) {
                    out.writeUTF("Length of login shouldn't be more than 30 symbols! Try again");
                    out.flush();
                } else if (roomName.equalsIgnoreCase("exit")) {
                    return false;
                } else if (roomService.createRoom(roomName, user, roomList)) {
                    out.writeUTF("The room was created successfully");
                    out.flush();
                    logger.info("Client " + user.getLogin() + " successfully created room \"" + roomName + "\"");
                    break;
                } else {
                    out.writeUTF("A room with this name already exists");
                    out.flush();
                }
            }
            currRoom = roomService.findRoomInList(roomList, roomName).get();
        } catch (IOException ignored) {
        }
        return true;
    }

    private String getRoomsNames() {
        StringBuilder roomNames = new StringBuilder();
        for (int i = 0; i < roomList.size(); i++) {
            roomNames.append(i + 1);
            roomNames.append(". ");
            roomNames.append(roomList.get(i).getName());
            if (i != roomList.size() - 1) {
                roomNames.append("\n");
            }
        }
        return roomNames.toString();
    }

    private boolean chooseRoom() {
        try {
            if (roomList.isEmpty()) {
                out.writeUTF("List of rooms is empty!");
                out.flush();
                return createRoom();
            }
            logger.info("Client " + user.getLogin() + " trying to choose room");
            while (true) {
                out.writeUTF("Rooms:     (for exiting write \"0\")");
                out.writeUTF(getRoomsNames());
                out.flush();
                try {
                    int roomNum = Integer.parseInt(in.readUTF());
                    if (roomNum == 0) {
                        return false;
                    }
                    Optional<Chatroom> room = roomService.chooseRoom(roomNum - 1, user, roomList);
                    if (room.isPresent()) {
                        currRoom = room.get();
                        logger.info("Client " + user.getLogin() + " chose room \"" + currRoom.getName() + "\"");
                        break;
                    }
                    out.writeUTF("There is no room with number " + roomNum);
                } catch (NumberFormatException ex) {
                    out.writeUTF("Unknown command. Please enter a number of room.");
                }
            }
        } catch (IOException ignored) {
        }
        return true;
    }
}
