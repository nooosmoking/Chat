package edu.school21.server;

import edu.school21.models.Message;
import edu.school21.models.User;
import edu.school21.models.UserWrapper;
import edu.school21.services.UsersService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SignUp implements Command {
    private final UsersService usersService;

    public SignUp( UsersService usersService){
        this.usersService = usersService;
    }

    @Override
    public void run(UserWrapper user) throws IOException {
        DataOutputStream out = user.getUser().getOut();
        DataInputStream in = user.getUser().getIn();
        out.writeUTF("Enter username:");
        out.flush();
        String username = in.readUTF();

        out.writeUTF("Enter password:");
        out.flush();
        String password = in.readUTF();
        if (usersService.signUp(username, password)){
            out.writeUTF("Successful!");
            user.getUser().setLogin(username);
            user.getUser().setActive(true);
        } else {
            out.writeUTF("Fail!");
        }
        out.flush();
    }
}
