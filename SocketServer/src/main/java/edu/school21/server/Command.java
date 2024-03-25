package edu.school21.server;

import edu.school21.models.UserWrapper;

import java.io.IOException;

public interface Command {
    void run(UserWrapper user) throws IOException;
}
