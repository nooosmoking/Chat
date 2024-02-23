package edu.school21.server;

import edu.school21.repositories.MessageRepository;
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
    private Socket client;
    private DataOutputStream out;
    private HashSet<DataOutputStream> sessions = new HashSet<>();
    private DataInputStream in;
    private final UsersService usersService;
    private final MessageRepository messageRepository;
    private final Map<String, Supplier<Command>> commandMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public Server(UsersService usersService, MessageRepository messageRepository) {
        this.usersService = usersService;
        this.messageRepository = messageRepository;
        commandMap.put("signin", () -> new SignIn(out, in, usersService, sessions));
        commandMap.put("signup", () -> new SignUp(out, in, usersService, sessions));
        commandMap.put("messaging", () -> new Messaging(out, in, sessions, messageRepository));
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
                startNewConnection();
                createCommandThread();
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

    private void startNewConnection() throws IOException {
        client = server.accept();
        out = new DataOutputStream(client.getOutputStream());
        in = new DataInputStream(client.getInputStream());

        out.writeUTF("Hello from Server!");
        out.flush();
    }

    private void createCommandThread() {
        new Thread(() -> {
            try {
                while(true){
                getCommand().run();}
            } catch (IOException ignored) {
            }
        }).start();
    }

    private Command getCommand() throws IOException {
        String entry;
        Command command = null;
        do {
            entry = in.readUTF().toLowerCase();
            try {
                command = commandMap.get(entry).get();
            } catch (NullPointerException e) {
                out.writeUTF("Unknown command. Please try again.");
            }
        } while (command == null);
        return command;
    }

    private void close() {
        try {
            in.close();
//            for (DataOutputStream out: allOutStreams){
//                out.close();
//            }
            client.close();
            System.exit(0);
        } catch (IOException | NullPointerException ignored) {
        }
    }
}
