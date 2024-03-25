package edu.school21.server;

import edu.school21.models.UserWrapper;
import edu.school21.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SignUp implements Command {
    private static final Logger logger = LoggerFactory.getLogger(SignUp.class);
    private final UsersService usersService;

    public SignUp( UsersService usersService){
        this.usersService = usersService;
    }

    @Override
    public void run(UserWrapper user) throws IOException {
        logger.info("New registration attempt");
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
        if (usersService.signUp(username, password)){
            out.writeUTF("Successful!");
            user.getUser().setLogin(username);
            user.getUser().setActive(true);
            logger.info("User " + username + " successfully registered");
            logger.info("User " + username + " successfully signed in");
        } else {
            out.writeUTF("User with this login is already exist!");
            logger.info("Error while signing up user " + username + ", user already exists");
        }
        out.flush();
    }
}
