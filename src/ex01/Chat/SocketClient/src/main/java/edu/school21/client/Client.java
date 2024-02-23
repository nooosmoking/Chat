package edu.school21.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Scanner scanner = new Scanner(System.in);
    private DataOutputStream out;
    private DataInputStream in;
    private Thread outputThread;

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
        String serverAnswer;
        try {
            do {
                serverAnswer = in.readUTF();
                System.out.println(serverAnswer);
            } while (!serverAnswer.equals("Successful!") && !serverAnswer.equals("Fail!"));
        } catch (IOException e) {
            System.err.println("Connection is closed.");
            System.exit(-1);
        }
    }

    private void startOutputThread() {
        outputThread = new Thread(() -> {
            while (true) {
                String answer = scanner.nextLine();
                if (answer.isEmpty()) {
                    continue;
                }
                try {
                    out.writeUTF(answer);
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Connection is closed.");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        });
        outputThread.start();
    }

    private void close() {
        try {
            out.close();
            in.close();
            scanner.close();
        } catch (IOException ignored) {
        }
        System.exit(0);
    }
}
