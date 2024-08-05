package com.espe.chatsdb.services;

import com.espe.chatsdb.models.entity.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    List<Message> list();
    Optional<Message> byId(Long id);
    Message save(Message message);
    void delete(Long id);
}
