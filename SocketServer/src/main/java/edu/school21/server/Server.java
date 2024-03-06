package edu.school21.server;

import edu.school21.models.Message;
import edu.school21.models.UserWrapper;
import edu.school21.repositories.MessageRepository;
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
    private Socket client;
    private DataOutputStream out;
    private final Map<DataOutputStream, BlockingQueue<Message>> clientMessageQueues = new ConcurrentHashMap<>();
    private DataInputStream in;
    private final Map<String, Supplier<Command>> commandMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);


    @Autowired
    public Server(UsersService usersService, MessageRepository messageRepository) {
        commandMap.put("signin", () -> new SignIn(out, in, usersService, clientMessageQueues));
        commandMap.put("signup", () -> new SignUp(out, in, usersService, clientMessageQueues));
        commandMap.put("messaging", () -> new Messaging(out, in, messageRepository, clientMessageQueues));
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
                client = server.accept();
            } catch (IOException ignored) {
            }
            createClientThread();
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

    private void createClientThread() {
        new Thread(() -> {
            UserWrapper currUser = new UserWrapper();
            try {
                DataOutputStream tmpOut = new DataOutputStream(client.getOutputStream());
                DataInputStream tmpIn = new DataInputStream(client.getInputStream());

                tmpOut.writeUTF("Hello from Server!");
                tmpOut.flush();
                while (true) {
                    getCommand(tmpOut, tmpIn ).run(currUser);
                }
            } catch (IOException | NullPointerException ignored) {
            }
        }).start();
    }

    private Command getCommand(DataOutputStream tmpOut, DataInputStream tmpIn) throws IOException {
        String entry;
        Command command = null;
        do {
            entry = tmpIn.readUTF().toLowerCase();
            try {
                if(entry.equals("exit")){
                    clientMessageQueues.remove(tmpOut);
                    tmpOut.writeUTF("You have left the chat.");
                    break;
                }
                out = tmpOut;
                in = tmpIn;
                command = commandMap.get(entry).get();
            } catch (NullPointerException e) {
                out.writeUTF("Unknown command. Please try again.");
            }
        } while (command == null);
        return command;
    }

    public void close() {
        try {
            in.close();
            for (DataOutputStream out : clientMessageQueues.keySet()) {
                out.close();
            }
            client.close();
            System.exit(0);
        } catch (IOException | NullPointerException ignored) {
        }
    }
}
