package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.MessageService;
import edu.school21.services.RoomService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Messaging implements Command {
    private DataOutputStream out;
    private DataInputStream in;
    private final List<Chatroom> roomList;
    private final MessageService messageService;
    private final RoomService roomService;
    private boolean isClosed = false;
    private User user;
    private String roomName;
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
    public void run(UserWrapper userWrapper) throws IOException {
        this.user = userWrapper.getUser();
        this.out = user.getOut();
        this.in = user.getIn();
        if (user.isActive()) {
            startMessaging();
            recieveMessagesFromClients();
            sendNewMessages();
            out.writeUTF("You have left the chat.");
        } else {
            out.writeUTF("You`re not logged in");
        }
        out.flush();
        user.setActive(false);
    }

    private void startMessaging() throws IOException {
        doRoomLogic();
//        out.writeUTF("Start messaging");
//        out.flush();
//        for (Message m: messageRepository.findLastCountMessages(30)) {
//            out.writeUTF(m.toString());
//        }
//        out.flush();
    }

    private void doRoomLogic() throws IOException {
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
        currRoom = roomService.findRoomInList(roomList, roomName).get();
    }

    private void createRoom() {
        try {
            out.writeUTF("Enter a name of chatroom:");
            out.flush();
            roomName = in.readUTF();
            if (roomService.createRoom(roomName, user, roomList)) {
                out.writeUTF("The room was created successfully");
                out.writeUTF(roomName + " ---");
                out.flush();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String getRoomsNames() {
        StringBuilder roomNames = new StringBuilder();
        for (int i = 0; i < roomList.size(); i++) {
            roomNames.append(i);
            roomNames.append(". ");
            roomNames.append(roomList.get(i));
            roomNames.append("\n");
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
                roomName = in.readUTF();
                if (roomService.chooseRoom(roomName, user, roomList)) {
                    break;
                } else {
                    out.writeUTF("There is no room " + roomName);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());;
        }

    }

    private void recieveMessagesFromClients() {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    String answer = in.readUTF();
//                    if(answer.equalsIgnoreCase("exit")){
//                        isClosed = true;
//                        break;
//                    }
//                    Message msg = new Message(user, answer, LocalDateTime.now());
//                    messageRepository.save(msg);
//                    for (Map.Entry<DataOutputStream, BlockingQueue<Message>> entry : clientMessageQueues.entrySet()) {
//                        entry.getValue().add(msg);
//                    }
//                } catch (IOException e) {
//                    System.err.println("Connection error");
//                }
//            }
//        }).start();
    }

    private void sendNewMessages() throws IOException {

//        while(!isClosed){
//            BlockingQueue<Message> currQueue = clientMessageQueues.get(out);
//            if(!currQueue.isEmpty()) {
//                try {
//                    out.writeUTF(currQueue.take().toString());
//                    out.flush();
//                } catch (InterruptedException ignored) {
//                }
//            }
//        }
    }
}
