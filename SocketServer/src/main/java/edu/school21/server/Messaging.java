package edu.school21.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.school21.models.Chatroom;
import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.MessageService;
import edu.school21.services.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class Messaging implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Messaging.class);
    private DataOutputStream out;
    private DataInputStream in;
    private final List<Chatroom> roomList;
    private final MessageService messageService;
    private final RoomService roomService;
    private User user;
    private Chatroom currRoom;
    private final Map<Integer, Supplier<Boolean>> commandMap = new HashMap<>();

    public Messaging(MessageService messageService, RoomService roomService, List<Chatroom> roomList) {
        this.messageService = messageService;
        this.roomService = roomService;
        this.roomList = roomList;
        commandMap.put(1, this::createRoom);
        commandMap.put(2, this::chooseRoom);
    }

    @Override
    public void run(UserWrapper userWrapper) {
        this.user = userWrapper.getUser();
        this.out = user.getOut();
        this.in = user.getIn();

        try {
            if (user.isActive()) {
                startMessaging();
                receiveMessageFromClient();
            }
        } catch (IOException ignored) {
        }
    }

    private void startMessaging() throws IOException {
        if (doRoomLogic()) {
            logger.info("Client " + user.getLogin() + " start messaging in room \"" + currRoom.getName() + "\"");
            for (Message m : messageService.findLastCountMessages(30, currRoom.getName())) {
                out.writeUTF(m.toJsonString());
            }
            out.flush();
        } else {removeUser();}
    }

    private boolean doRoomLogic() throws IOException {
        out.writeUTF("1. Create room\n" + "2. Choose room\n" + "3. Exit");
        out.flush();
        int answer;
        while (true) {
            try {
                answer = Integer.parseInt(in.readUTF());
                if (answer == 3) {
                    logger.info("Client " + user.getLogin() + " left chat");
                    return false;
                } else if (!commandMap.get(answer).get()){
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
                } else if (roomName.equals("exit")) {
                    return false;
                } else if (roomService.createRoom(roomName, user, roomList)) {
                    out.writeUTF("The room was created successfully");
                    out.flush();
                    logger.info("Client " + user.getLogin() + " successfully created room \"" + roomName + "\"");
                    break;

                } else {
                    out.writeUTF("A room with this name already exists");
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
                    if (roomNum == 0){
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

    private void receiveMessageFromClient() throws IOException {
        while (true) {
            String answerJson = in.readUTF();
            try {
                Message msg = new Message(answerJson, user, currRoom);
                if (msg.getText().equals("exit")) {
                    out.writeUTF("You have left the chat.");
                    logger.info("Client " + user.getLogin() + " left the chatroom \"" + currRoom.getName() + "\"");
                    removeUser();
                    return;
                } else if (msg.getText().length() > 3000) {
                    out.writeUTF("Length of message shouldn't be more than 3000 symbols! Try again");
                    out.flush();
                    continue;
                }
                messageService.save(msg);
                sendMessage(answerJson);
            } catch (JsonProcessingException | NoSuchElementException ignored) {
                logger.error("Error while deserialization json message");
            }
        }
    }

    private void sendMessage(String messageJson) {
        currRoom.getUserList().stream().map(User::getOut).forEach(o -> {
            try {
                o.writeUTF(messageJson);
            } catch (IOException e) {
                logger.error("Error while sending message");
            }
        });
    }

    private void removeUser() {
        try {
            out.writeUTF("You have left the chat.");
        } catch (IOException ignored) {
        }
        user.setActive(false);
        logger.warn("Client " + user.getLogin() + " disconnected");
        currRoom.getUserList().remove(user);
        try {
            user.getIn().close();
        } catch (IOException ignored) {
        }
        try {
            user.getOut().close();
        } catch (IOException ignored) {
        }
    }
}
