package edu.school21.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

public interface Command {
    void run() throws IOException;
}
