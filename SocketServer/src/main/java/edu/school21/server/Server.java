package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.Message;
import edu.school21.models.UserWrapper;
import edu.school21.repositories.MessageRepository;
import edu.school21.repositories.RoomRepository;
import edu.school21.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class Server {
    private ServerSocket server;
    private final Map<String, Supplier<Command>> commandMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private List<Chatroom> chatrooms;

    @Autowired
    public Server(UsersService usersService, MessageRepository messageRepository, RoomRepository roomRepository) {
        commandMap.put("signin", () -> new SignIn(usersService));
        commandMap.put("signup", () -> new SignUp(usersService));
        commandMap.put("messaging", () -> new Messaging(messageRepository, chatrooms));
        chatrooms = roomRepository.findAll();
    }

    public void setPort(int port) {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void run() {
        startStdin();
        while (true) {
            try {
                Socket client = server.accept();
                new ClientThread(client).start();
            } catch (IOException ignored) {
            }

        }
    }

    private void startStdin() {
        Thread stdin = new Thread(() -> {
            String answer = scanner.nextLine();
            if (answer.equals("stop")) {
                close();
            }
        });
        stdin.start();
    }

    private class ClientThread extends Thread {
        private Socket clientSocket;

        public ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                UserWrapper currUser = new UserWrapper(out, in);
                out.writeUTF("Hello from Server!");
                out.flush();
                while (true) {
                    getCommand(out, in ).run(currUser);
                }
            } catch (IOException | NullPointerException ignored) {
            }
        }
    }

    private synchronized Command getCommand(DataOutputStream out, DataInputStream in) throws IOException {
        String entry;
        Command command = null;
        do {
            entry = in.readUTF().toLowerCase();
            try {
                if(entry.equals("exit")){
                    out.writeUTF("You have left the chat.");
                    break;
                }
                command = commandMap.get(entry).get();
            } catch (NullPointerException e) {
                out.writeUTF("Unknown command. Please try again.");
            }
        } while (command == null);
        return command;
    }

    public void close() {
//        try {
//            in.close();
//            for (DataOutputStream out : clientMessageQueues.keySet()) {
//                out.close();
//            }
//            client.close();
//            System.exit(0);
//        } catch (IOException | NullPointerException ignored) {
//        }
    }
}
