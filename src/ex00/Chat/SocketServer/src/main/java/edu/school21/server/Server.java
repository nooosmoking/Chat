package edu.school21.server;

import edu.school21.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class Server {
    private UsersService usersService;
    private ServerSocket server;
    private Socket client;
    private DataOutputStream out;
    private DataInputStream in;

    @Autowired
    public Server(UsersService usersService) {
        this.usersService = usersService;
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
        try {
            client = server.accept();
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());

            out.writeUTF("Hello from Server!");
            out.flush();

            String entry;
            do {
                entry = in.readUTF();
            } while (!entry.equals("signUp"));

            out.writeUTF("Enter username:");
            out.flush();
            String username = in.readUTF();

            out.writeUTF("Enter password:");
            out.flush();
            String password = in.readUTF();

            if (usersService.signUp(username, password)){
                out.writeUTF("Successful!");
            } else {
                out.writeUTF("Fail!");
            }
            out.flush();
            close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void close() throws IOException {
        in.close();
        out.close();
        client.close();
    }
}
