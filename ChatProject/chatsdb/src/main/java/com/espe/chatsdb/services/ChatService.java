package com.espe.chatsdb.services;

import com.espe.chatsdb.models.entity.Chat;
import com.espe.chatsdb.models.entity.Message;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    List<Chat> list();
    Optional<Chat> byId(Long id);
    Chat save(Chat chat);
    void delete(Long id);

    Optional<Message> addMessage(Message message,Long idChat);
    Optional<Message> deleteMessage(Message Message,Long idChat);
    List<Message> getChatMessages(Long chatId);

}
