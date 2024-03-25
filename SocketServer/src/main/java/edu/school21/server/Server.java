package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.MessageService;
import edu.school21.services.RoomService;
import edu.school21.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Supplier;

@Component
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private ServerSocket server;
    private final Map<Integer, Supplier<Command>> commandMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private List<Chatroom> chatrooms;
    private boolean isStop = false;
    private final RoomService roomService;

    @Autowired
    public Server(UsersService usersService, MessageService messageService, RoomService roomService) {
        this.roomService = roomService;
        chatrooms = roomService.findAllRooms();
        commandMap.put(1, () -> new SignIn(usersService));
        commandMap.put(2, () -> new SignUp(usersService));
        commandMap.put(0, () -> new Messaging(messageService, roomService, chatrooms));
    }

    public void setPort(int port) {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Error while starting server.");
            System.exit(-1);
        }
    }

    public void run() {
        logger.info("Starting server. For exiting write \"stop\"");
        startStdin();
        startRoomUpdater();
        while (true) {
            try {
                Socket client = server.accept();
                new ClientThread(client).start();
            } catch (IOException e) {
                if (!isStop) {
                    logger.error("Error while connecting client");
                }
            }
        }
    }

    private void startStdin() {
        Thread stdin = new Thread(() -> {
            while (true) {
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("stop")) {
                    isStop = true;
                    close();
                }
            }
        });
        stdin.start();
    }

    private void startRoomUpdater() {
        Thread roomUpdater = new Thread(() -> {
            while (true) {
                List<Chatroom> tmpRooms = roomService.findAllRooms();
                List<Chatroom> newRooms = RoomManager.findDifferentChatrooms(chatrooms, tmpRooms);
                if(newRooms!=null){
                    chatrooms.addAll(newRooms);}
            }
        });
        roomUpdater.start();
    }

    private class ClientThread extends Thread {
        private final Socket clientSocket;

        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                UserWrapper currUser = new UserWrapper(out, in);
                logger.info("New client connected");
                out.writeUTF("Hello from Server!\n1. SignIn\n2. SignUp\n3. Exit");
                out.flush();
                while (true) {
                    getCommand(out, in).run(currUser);
                }
            } catch (IOException | NullPointerException ignored) {
            }
        }

        private Command getCommand(DataOutputStream out, DataInputStream in) throws IOException {
            int entry;
            Command command = null;
            do {
                try {
                    entry = Integer.parseInt(in.readUTF());
                    if (entry == 3) {
                        out.writeUTF("You have left the chat.");
                        out.flush();
                        out.close();
                        in.close();
                        logger.info("Client disconnected");
                        break;
                    }
                    command = commandMap.get(entry).get();
                } catch (NullPointerException | NumberFormatException e) {
                    out.writeUTF("Unknown command." + " Please try again.");
                }
            } while (command == null);
            return command;
        }
    }

    public void close() {
        try {
            chatrooms.stream().map(Chatroom::getUserList).forEach(l -> l.stream().map(User::getIn).forEach(in -> {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }));
            chatrooms.stream().map(Chatroom::getUserList).forEach(l -> l.stream().map(User::getOut).forEach(out -> {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }));
            server.close();
            scanner.close();
            System.exit(0);
        } catch (IOException | NullPointerException ignored) {
        }
    }
}
