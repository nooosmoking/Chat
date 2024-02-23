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
        try {
            startOutputThread();
            readInput();
            close();
        } catch (IOException ignored) {
        }
    }

    private void readInput() throws IOException {
        String serverAnswer;
        do {
            serverAnswer = in.readUTF();
            System.out.println(serverAnswer);
        } while (!serverAnswer.equals("You have left the chat."));
    }

    private void startOutputThread() {
        outputThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                String answer = scanner.nextLine();
                if(answer.isEmpty()){
                    continue;
                }
                try {
                    out.writeUTF(answer);
                    out.flush();
                } catch (IOException e){
                    System.err.println(e.getMessage());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.exit(-1);
                }
            }
        });
        outputThread.start();
    }

    private void close() throws IOException {
        outputThread.interrupt();
        out.close();
        in.close();
        scanner.close();
    }
}
