package edu.school21.services;

import edu.school21.models.Message;
import edu.school21.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> findLastCountMessages(int count) {
        return messageRepository.findLastCountMessages(count);
    }

    @Override
    public boolean save(Message msg) {
        return messageRepository.save(msg);
    }
}
