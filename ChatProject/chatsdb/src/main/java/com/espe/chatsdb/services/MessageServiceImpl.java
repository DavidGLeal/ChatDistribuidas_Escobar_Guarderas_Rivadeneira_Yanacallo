package com.espe.chatsdb.services;

import com.espe.chatsdb.clients.UserClientRest;
import com.espe.chatsdb.models.User;
import com.espe.chatsdb.models.entity.Message;
import com.espe.chatsdb.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    private MessageRepository repository;
    @Autowired
    private UserClientRest userClientRest;

    private Message addUsName(Message message) {
        String user = userClientRest.detail(message.getUserId()).getName();
        message.setUserName(user);
        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> list() {
        List<Message> messages = (List<Message>) repository.findAll();

        for(Message ms:messages) {
            ms = addUsName(ms);
        }
        return messages;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> byId(Long id) {
        return Optional.of(addUsName(repository.findById(id).get()));
    }

    @Override
    @Transactional
    public Message save(Message message) {
        return repository.save(message);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
