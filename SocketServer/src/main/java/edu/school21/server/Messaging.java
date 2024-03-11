package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.MessageService;
import edu.school21.services.RoomService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

public class Messaging implements Command {
    private DataOutputStream out;
    private DataInputStream in;
    private final List<Chatroom> roomList;
    private final MessageService messageService;
    private final RoomService roomService;
    private User user;
    private Chatroom currRoom;
    private final Map<Integer, Supplier<Void>> commandMap = new HashMap<>();

    public Messaging(MessageService messageService, RoomService roomService, List<Chatroom> roomList) {
        this.messageService = messageService;
        this.roomService = roomService;
        this.roomList = roomList;
        commandMap.put(1, () -> {
            createRoom();
            return null;
        });
        commandMap.put(2, () -> {
            chooseRoom();
            return null;
        });
    }

    @Override
    public void run(UserWrapper userWrapper) throws IOException, NoSuchElementException {
        this.user = userWrapper.getUser();
        this.out = user.getOut();
        this.in = user.getIn();
        if (user.isActive()) {
            startMessaging();
            recieveMessageFromClient();
            out.writeUTF("You have left the chat.");
        } else {
            out.writeUTF("You`re not logged in");
        }
        out.flush();
        user.setActive(false);
    }

    private void startMessaging() throws IOException, NoSuchElementException {
        doRoomLogic();
        out.writeUTF(currRoom.getName() + " ---");
        for (Message m : messageService.findLastCountMessages(30, currRoom.getName())) {
            out.writeUTF(m.toString());
        }
        out.flush();
    }

    private void doRoomLogic() throws IOException, NoSuchElementException {
        out.writeUTF("1. Create room\n" +
                "2. Choose room\n" +
                "3. Exit");
        out.flush();
        int answer;
        while (true) {
            try {
                answer = Integer.parseInt(in.readUTF());
                if (answer == 3) {
                    out.writeUTF("You have left the chat.");
                    return;
                } else {
                    commandMap.get(answer).get();
                    break;
                }
            } catch (NullPointerException | NumberFormatException e) {
                out.writeUTF("Unknown command." +
                        " Please try again.");
            }
        }
    }

    private void createRoom() {
        try {
            String roomName;
            while (true) {
                out.writeUTF("Enter a name of chatroom:");
                out.flush();
                roomName = in.readUTF();
                if (roomService.createRoom(roomName, user, roomList)) {
                    out.writeUTF("The room was created successfully");
                    break;
                } else {
                    out.writeUTF("A room with this name already exists");
                }
            }
            currRoom = roomService.findRoomInList(roomList, roomName).get();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String getRoomsNames() {
        StringBuilder roomNames = new StringBuilder();
        for (int i = 0; i < roomList.size(); i++) {
            roomNames.append(i);
            roomNames.append(". ");
            roomNames.append(roomList.get(i).getName());
            if (i != roomList.size() - 1) {
                roomNames.append("\n");
            }
        }
        return roomNames.toString();
    }

    private void chooseRoom() {
        try {
            if (roomList.isEmpty()) {
                out.writeUTF("List of rooms is empty!");
                out.flush();
                createRoom();
                return;
            }
            while (true) {
                out.writeUTF("Rooms:");
                out.writeUTF(getRoomsNames());
                out.flush();
                int i = Integer.parseInt(in.readUTF());
                Optional<Chatroom> room = roomService.chooseRoom(i, user, roomList);
                if (room.isPresent()) {
                    currRoom = room.get();
                    break;}
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void recieveMessageFromClient() throws IOException {
        while (true) {
                String answer = in.readUTF();
                if (answer.equalsIgnoreCase("exit")) {
                    currRoom.getUserList().remove(user);
                    break;
                }
                Message msg = new Message(user, answer, LocalDateTime.now(), currRoom);
                messageService.save(msg);
                sendMessage(msg);
        }
    }

    private void sendMessage(Message msg) throws IOException {
        currRoom.getUserList().stream().map(User::getOut).forEach(o -> {
            try {
                o.writeUTF(msg.toString());
            } catch (IOException ignored) {
            }
        });

    }
}
