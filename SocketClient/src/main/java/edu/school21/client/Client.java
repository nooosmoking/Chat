package edu.school21.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.models.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Client {
    private final Scanner scanner = new Scanner(System.in);
    private DataOutputStream out;
    private DataInputStream in;
    private boolean askUsernameServer = false;
    private String username;
    private boolean askChatroomServer = false;
    private String chatroom;
    private boolean isMessaging = false;

    public Client(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void run() {
        startOutputThread();
        readInput();
        close();
    }

    private void readInput() {
        try {
            while (showMessage(in.readUTF())) {
            }
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Connection is closed.");
            System.exit(-1);
        }
    }

    private boolean showMessage(String serverAnswer) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(serverAnswer);
            Message msg = new Message(serverAnswer);
            System.out.println(msg);
        } catch (JsonProcessingException e) {
            System.out.println(serverAnswer);
            if (serverAnswer.equals("Successful!")) {
                out.writeUTF("0");
                out.flush();
            }
            if (serverAnswer.equals("You have left the chat.")) {
                return false;
            }
            if (serverAnswer.equals("Enter username:")) {
                askUsernameServer = true;
            }
            if (askChatroomServer) {
                chatroom = serverAnswer;
                askChatroomServer = false;
            }
            if (serverAnswer.startsWith("Rooms")) {
                askChatroomServer = true;
            }
            if (serverAnswer.endsWith("---")) {
                isMessaging = true;
            }
        }
        return true;
    }

    private void startOutputThread() {
        new Thread(() -> {
            while (true) {
                String answer = scanner.next();
                if (answer.isEmpty()) {
                    continue;
                }
                if (askUsernameServer) {
                    username = answer;
                    askUsernameServer = false;
                }
                try {
                    if (isMessaging) {
                        Message msg = new Message(username, answer, LocalDateTime.now(), chatroom);
                        out.writeUTF(msg.toJsonString());
                    } else {
                        out.writeUTF(answer);
                    }
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Connection is closed.");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    private void close() {
        try {
            out.close();
        } catch (IOException ignored) {
        }
        try {
            in.close();
        } catch (IOException ignored) {
        }
        scanner.close();
        System.exit(0);
    }
}
