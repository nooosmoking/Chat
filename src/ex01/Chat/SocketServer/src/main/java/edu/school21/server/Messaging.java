package edu.school21.server;

import edu.school21.repositories.MessageRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class Messaging implements Command{
    private final DataOutputStream out;
    private final DataInputStream in;
    private final HashSet<DataOutputStream> sessions;
    private final MessageRepository messageRepository;

    public Messaging(DataOutputStream out, DataInputStream in, HashSet<DataOutputStream> sessions, MessageRepository messageRepository) {
        this.in = in;
        this.out = out;
        this.sessions = sessions;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run() throws IOException {
        if(sessions.contains(out)){
            System.out.println(messageRepository.findAll());
        }
    }
}

//Select * from message where create_date  %date;
//
//1. Нужно выгружать данные из базы через сдвиг даты.
//2. Тоесть делаешь select new date, и так по кругу.