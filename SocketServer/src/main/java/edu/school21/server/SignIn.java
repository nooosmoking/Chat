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
    private final DataOutputStream out;
    private final DataInputStream in;
    private final UsersService usersService;
    private final Map<DataOutputStream, BlockingQueue<Message>> clientMessageQueues;


    public SignIn(DataOutputStream out, DataInputStream in, UsersService usersService, Map<DataOutputStream, BlockingQueue<Message>> clientMessageQueues){
        this.in = in;
        this.out = out;
        this.usersService = usersService;
        this.clientMessageQueues = clientMessageQueues;
    }

    @Override
    public void run(UserWrapper user) throws IOException {
        out.writeUTF("Enter username:");
        out.flush();
        String username = in.readUTF();

        out.writeUTF("Enter password:");
        out.flush();
        String password = in.readUTF();
        User currUser = usersService.signIn(username, password);
        if (currUser != null){
            out.writeUTF("Successful!");
            clientMessageQueues.put(out, new LinkedBlockingDeque<>());
            user.setUser(currUser);
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