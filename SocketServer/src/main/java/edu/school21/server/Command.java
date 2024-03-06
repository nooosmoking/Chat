package edu.school21.server;

import edu.school21.models.User;
import edu.school21.models.UserWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

public interface Command {
    void run(UserWrapper user) throws IOException;
}
