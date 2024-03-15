package edu.school21.server;

import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.UsersService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SignIn implements Command {
    private final UsersService usersService;

    public SignIn(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public void run(UserWrapper user) throws IOException {
        System.out.println("New sign attempt");
        DataOutputStream out = user.getUser().getOut();
        DataInputStream in = user.getUser().getIn();
        out.writeUTF("Enter username:");
        out.flush();
        String username = in.readUTF();
        if (username.length() > 30){
            out.writeUTF("Length of login shouldn't be more than 30 symbols! Try again");
            out.writeUTF("1. signIn\n2. SignUp\n3. Exit");
            out.flush();
            return;
        }
        out.writeUTF("Enter password:");
        out.flush();
        String password = in.readUTF();
        if (usersService.signIn(username, password)) {
            out.writeUTF("Successful!");
            user.getUser().setLogin(username);
            user.getUser().setActive(true);
            System.out.println("User " + username + " successfully signed in");
        } else {
            out.writeUTF("Fail!");
            System.out.println("Error while signing in user " + username);
        }
        out.flush();
    }
}