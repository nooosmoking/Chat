package edu.school21.server;

import edu.school21.models.Chatroom;
import edu.school21.models.UserWrapper;
import edu.school21.services.MessageService;
import edu.school21.services.RoomService;
import edu.school21.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Supplier;

@Component
public class Server {
    private ServerSocket server;
    private final Map<Integer, Supplier<Command>> commandMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private List<Chatroom> chatrooms;

    @Autowired
    public Server(UsersService usersService, MessageService messageService, RoomService roomService) {
        chatrooms = roomService.findAllRooms();
        commandMap.put(1, () -> new SignIn(usersService));
        commandMap.put(2, () -> new SignUp(usersService));
        commandMap.put(0, () -> new Messaging(messageService, roomService, chatrooms));
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
            } catch (IOException e) {
                System.err.println(e.getMessage());
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
                out.writeUTF("Hello from Server!\n1. signIn\n2. SignUp\n3. Exit");
                out.flush();
                while (true) {
                    getCommand(out, in ).run(currUser);
                }
            } catch (IOException | NullPointerException ignored){
            }
        }

        private Command getCommand(DataOutputStream out, DataInputStream in) throws IOException {
            int entry;
            Command command = null;
            do {
                try {
                    entry = Integer.parseInt(in.readUTF());
                    if(entry==3){
                        out.writeUTF("You have left the chat.");
                        break;
                    }
                    command = commandMap.get(entry).get();
                } catch (NullPointerException | NumberFormatException e) {
                    out.writeUTF("Unknown command." +
                            " Please try again.");
                }
            } while (command == null);
            return command;
        }
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
