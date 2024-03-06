package edu.school21.server;

import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.repositories.MessageRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Messaging implements Command{
    private final DataOutputStream out;
    private final DataInputStream in;
    private final  Map<DataOutputStream, BlockingQueue<Message>> clientMessageQueues;
    private final MessageRepository messageRepository;
    private boolean isClosed = false;
    private User user;

    public Messaging(DataOutputStream out, DataInputStream in, MessageRepository messageRepository, Map<DataOutputStream, BlockingQueue<Message>> clientMessageQueues) {
        this.in = in;
        this.out = out;
        this.clientMessageQueues = clientMessageQueues;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(UserWrapper user)throws IOException {
        this.user=user.getUser();
        if(clientMessageQueues.containsKey(out)){
            startMessaging();
            recieveMessagesFromClients();
            sendNewMessages();
            out.writeUTF("You have left the chat.");
        } else {
            out.writeUTF("You`re not logged in");
        }
        out.flush();
        clientMessageQueues.remove(out);
    }

    private void startMessaging() throws IOException {
        out.writeUTF("Start messaging");
        out.flush();
        for (Message m: messageRepository.findLastCountMessages(30)) {
            out.writeUTF(m.toString());
        }
        out.flush();
    }

    private void recieveMessagesFromClients(){
        new Thread(() -> {
            while (true) {
                try {
                    String answer = in.readUTF();
                    if(answer.equalsIgnoreCase("exit")){
                        isClosed = true;
                        break;
                    }
                    Message msg = new Message(0L, user, answer, LocalDateTime.now());
                    messageRepository.save(msg);
                    for (Map.Entry<DataOutputStream, BlockingQueue<Message>> entry : clientMessageQueues.entrySet()) {
                        entry.getValue().add(msg);
                    }
                } catch (IOException e) {
                    System.err.println("Connection error");
                }

            }
        }).start();
    }

    private void sendNewMessages() throws IOException {
        while(!isClosed){
            BlockingQueue<Message> currQueue = clientMessageQueues.get(out);
            if(!currQueue.isEmpty()) {
                try {
                    out.writeUTF(currQueue.take().toString());
                    out.flush();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
