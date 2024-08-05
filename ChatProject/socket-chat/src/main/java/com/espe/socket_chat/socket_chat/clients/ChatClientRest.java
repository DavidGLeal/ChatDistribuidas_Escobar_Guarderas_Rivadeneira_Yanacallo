package com.espe.socket_chat.socket_chat.clients;

import com.espe.socket_chat.socket_chat.model.Chat;
import com.espe.socket_chat.socket_chat.model.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="chatsdb", url="localhost:8002/chats")
public interface ChatClientRest {
    @GetMapping("/{id}")
    Chat detail(@PathVariable Long id);

    @PostMapping
    Chat create(@RequestBody Chat chat);

    @PostMapping("/{chatId}/messages")
    Message addMessage(@RequestBody Message message,@PathVariable Long chatId);

    @GetMapping("/{chatId}/messages")
    List<Message> getChatMessages(@PathVariable Long chatId);

}
