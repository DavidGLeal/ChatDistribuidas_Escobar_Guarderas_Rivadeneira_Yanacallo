package com.espe.socket_chat.socket_chat.controller;

import com.espe.socket_chat.socket_chat.clients.ChatClientRest;
import com.espe.socket_chat.socket_chat.clients.UserClientRest;
import com.espe.socket_chat.socket_chat.model.Chat;
import com.espe.socket_chat.socket_chat.model.Message;
import com.espe.socket_chat.socket_chat.model.MessageType;
import com.espe.socket_chat.socket_chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.AbstractDestinationResolvingMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.HTML;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.espe.socket_chat.socket_chat.model.MessageType.JOIN;
import static com.espe.socket_chat.socket_chat.model.MessageType.LEAVE;

@Controller
public class WebSocketController {

    @Autowired
    private ChatClientRest chatClientRest;
    @Autowired
    private UserClientRest userClientRest;

    private final Map<Long, Map<String, String>> chatUsers = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat/{chatId}")
    @SendTo("/topic/{chatId}")
    public Message chat(@DestinationVariable Long chatId, @Payload Message message){

        System.out.println(message.getUserId()+":"+message.getMessageText());
        Chat chat = chatClientRest.detail(chatId);
        String userName = userClientRest.detail(message.getUserId()).getName();
        if(chat!=null){
            Message newMessage = chatClientRest.addMessage(message,chat.getId());
            newMessage.setUserName(userName);
            //Chat updatedChat = chatClientRest.detail(chat.getId());
            return newMessage;
        }
        /*System.out.println(message.getUserId() +": "+ message.getMessageText());
        return new Message(message.getMessageText(), message.getUserId());*/
        return null;
    }

    @MessageMapping("/chat/{chatId}/addUser")
    @SendTo("/topic/{chatId}")
    public Message addUser(@DestinationVariable Long chatId,
                           @Payload Message message,
                           SimpMessageHeaderAccessor headerAccessor){
        System.out.println(message.getUserId()+" - "+message.getUserName());
        headerAccessor.getSessionAttributes().put("userName",message.getUserName());
        headerAccessor.getSessionAttributes().put("userId",message.getUserId());
        headerAccessor.getSessionAttributes().put("chatId",chatId);
        addUserToChat(chatId,message.getUserName(),message.getUserId().toString());
        message.setType(JOIN);
        return message;
    }

    @MessageMapping("/chat/{chatId}/leaveUser")
    @SendTo("/topic/{chatId}")
    public Message leaveUser(@DestinationVariable Long chatId,
                             @Payload Message message,
                             SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("userName");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (username != null) {
            chatUsers.getOrDefault(chatId, new ConcurrentHashMap<>()).remove(username);
            var leaveMessage = Message.builder()
                    .type(LEAVE)
                    .userId(userId)
                    .userName(username)
                    .build();
            return leaveMessage;
        }
        message.setType(LEAVE);
        return message;
    }



    @MessageMapping("/chat/{chatId}/getUsers")
    @SendTo("/topic/{chatId}/users")
    public Map<String,String> getUsers(@DestinationVariable Long chatId){
        System.out.println(chatUsers.getOrDefault(chatId, new ConcurrentHashMap<>()));
        return chatUsers.getOrDefault(chatId, new ConcurrentHashMap<>());
    }

    public void addUserToChat(Long chatId,String username,String userId){
        chatUsers.computeIfAbsent(chatId,k -> new ConcurrentHashMap<>()).put(username,userId);
    }

    @MessageMapping("/chat/{chatId}/notify")
    public void notify(@DestinationVariable Long chatId, @Payload Message message){
        Message notification= Message.builder()
                .type(MessageType.NOTIFICATION)
                .chatId(chatId)
                .userId(message.getUserId())
                .userName(userClientRest.detail(message.getUserId()).getName())
                .messageText(message.getMessageText())
                .build();
        System.out.println(notification.getChatId());
        messagingTemplate.convertAndSend("/topic/notifications",notification);
    }

}
