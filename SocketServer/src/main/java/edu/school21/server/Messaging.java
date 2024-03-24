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

public class Messaging implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Messaging.class);
    private DataOutputStream out;
    private DataInputStream in;
    private final MessageService messageService;
    private User user;
    private final RoomManager roomManager;

    public Messaging(MessageService messageService, RoomService roomService, List<Chatroom> roomList) {
        this.messageService = messageService;
        this.roomManager = new RoomManager(roomList, roomService);
    }

    @Override
    public void run(UserWrapper userWrapper) {
        this.user = userWrapper.getUser();
        this.out = user.getOut();
        this.in = user.getIn();
        roomManager.setUser(userWrapper);

        try {
            if (user.isActive()) {
                startMessaging();
                receiveMessageFromClient();
            }
        } catch (IOException ignored) {
        }
    }

    private void startMessaging() throws IOException {
        if (roomManager.doRoomLogic()) {
            logger.info("Client " + user.getLogin() + " start messaging in room \"" + roomManager.getCurrRoom().getName() + "\"");
            for (Message m : messageService.findLastCountMessages(30, roomManager.getCurrRoom().getName())) {
                out.writeUTF(m.toJsonString());
            }
            out.flush();
        } else {
            removeUser();
        }
    }

    private void receiveMessageFromClient() throws IOException {
        while (true) {
            String answerJson = in.readUTF();
            try {
                Message msg = new Message(answerJson, user, roomManager.getCurrRoom());
                if (msg.getText().equals("exit")) {
                    out.writeUTF("You have left the chat.");
                    logger.info("Client " + user.getLogin() + " left the chatroom \"" + roomManager.getCurrRoom().getName() + "\"");
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
        roomManager.getCurrRoom().getUserList().stream().map(User::getOut).forEach(o -> {
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
        roomManager.getCurrRoom().getUserList().remove(user);
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
