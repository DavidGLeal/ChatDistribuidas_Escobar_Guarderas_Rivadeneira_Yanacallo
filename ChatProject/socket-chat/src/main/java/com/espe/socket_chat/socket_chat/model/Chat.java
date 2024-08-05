package com.espe.socket_chat.socket_chat.model;

import java.util.List;

public class Chat {
    private Long id;

    private String title;
    private List<Message> messages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message){ messages.add(message);}

    public void removeMessage(Message message){messages.remove(message);}

}
