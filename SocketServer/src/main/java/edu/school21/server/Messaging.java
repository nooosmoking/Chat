package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.repositories.MessageRepository;
import edu.school21.services.MessageService;
import edu.school21.services.RoomService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Messaging implements Command{
    private DataOutputStream out;
    private DataInputStream in;
    private final List<Chatroom> roomList;
    private final MessageService messageService;
    private final RoomService roomService;
    private boolean isClosed = false;
    private User user;

    public Messaging(MessageService messageService, RoomService roomService, List<Chatroom> roomList) {
        this.messageService = messageService;
        this.roomService = roomService;
        this.roomList = roomList;
    }

    @Override
    public void run(UserWrapper userWrapper)throws IOException {
//        this.user=userWrapper.getUser();
//        this.out = user.getOut();
//        this.in = user.getIn();
//        if(clientMessageQueues.containsKey(out)){
//            startMessaging();
//            recieveMessagesFromClients();q
//            sendNewMessages();
//            out.writeUTF("You have left the chat.");
//        } else {
//            out.writeUTF("You`re not logged in");
//        }
//        out.flush();
//        clientMessageQueues.remove(out);
    }

//    private void startMessaging() throws IOException {
//        out.writeUTF("Start messaging");
//        out.flush();
//        for (Message m: messageRepository.findLastCountMessages(30)) {
//            out.writeUTF(m.toString());
//        }
//        out.flush();
//    }
//
//    private void recieveMessagesFromClients(){
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
//    }
//
//    private void sendNewMessages() throws IOException {
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
//    }
}
