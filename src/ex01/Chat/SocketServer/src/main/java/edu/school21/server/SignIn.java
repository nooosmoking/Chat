package edu.school21.server;

import edu.school21.services.UsersService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class SignIn implements Command {
    private final DataOutputStream out;
    private final DataInputStream in;
    private final UsersService usersService;
    private final HashSet<DataOutputStream> sessions;


    public SignIn(DataOutputStream out, DataInputStream in, UsersService usersService, HashSet<DataOutputStream> sessions){
        this.in = in;
        this.out = out;
        this.usersService = usersService;
        this.sessions = sessions;
    }

    @Override
    public void run() throws IOException {
        out.writeUTF("Enter username:");
        out.flush();
        String username = in.readUTF();

        out.writeUTF("Enter password:");
        out.flush();
        String password = in.readUTF();

        if (usersService.signIn(username, password)){
            out.writeUTF("Successful!");
            sessions.add(out);
        } else {
            out.writeUTF("Fail!");
        }
        out.flush();
    }
}
//Паттерн https://www.baeldung.com/java-observer-pattern
//        1. После успешного создания сообщения происходит сигнал для рассылки.
//        2. Пусть в базе отдельно будут счетчики.
//        3. Пусть есть задача которая ходит в базу раз 0.1 секунды и проверяет их. В случае переполнени зачищяет.