package com.espe.chatsdb.services;

import com.espe.chatsdb.models.entity.Chat;
import com.espe.chatsdb.models.entity.Message;
import com.espe.chatsdb.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private ChatRepository repository;

    @Autowired
    private MessageService messageService;

    @Override
    @Transactional(readOnly=true)
    public List<Chat> list() {
        List<Chat> chats = (List<Chat>) repository.findAll();

        for(Chat c: chats){
            c.setMessages(getChatMessages(c.getId()));
        }
        return chats;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Chat> byId(Long id) {
        Optional<Chat> chat = repository.findById(id);
        chat.get().setMessages(getChatMessages(id));
        return chat;
    }

    @Override
    @Transactional
    public Chat save(Chat chat) {
        return repository.save(chat);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Message> addMessage(Message message,Long idChat) {
        Optional<Chat> o = repository.findById(idChat);

        if(o.isPresent()){

            o.get().addMessage(messageService.save(message));

            repository.save(o.get());
            return Optional.of(o.get().getMessages().get(o.get().getMessages().size()-1));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> deleteMessage(Message message,Long idChat) {
        Optional<Chat> o = repository.findById(idChat);
        if(o.isPresent() && o.get().getMessages().contains(message)){
            o.get().removeMessage(message);
            messageService.delete(message.getId());
            repository.save(o.get());
        }
        return Optional.empty();
    }

    @Override
    public List<Message> getChatMessages(Long chatId) {
        Optional<Chat> chatOptional = repository.findById(chatId);
        if(chatOptional.isPresent()){
            Chat chat = chatOptional.get();
            List<Message> messages = chat.getMessages();
            for(Message m:messages){
                m = messageService.byId(m.getId()).get();
            }
            return messages;
        }
        return List.of();
    }
}
