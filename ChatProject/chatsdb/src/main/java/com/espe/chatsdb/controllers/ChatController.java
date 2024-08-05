package com.espe.chatsdb.controllers;


import com.espe.chatsdb.clients.UserClientRest;
import com.espe.chatsdb.models.entity.Chat;
import com.espe.chatsdb.models.entity.Message;
import com.espe.chatsdb.services.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/chats")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ChatController {

    @Autowired
    private ChatService service;

    @GetMapping
    public ResponseEntity<List<Chat>> list(){
        List<Chat> chats = service.list();
        if(chats.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(null);
        }
        return ResponseEntity.ok()
                .body(chats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){
        Optional<Chat> chatOptional = service.byId(id);
        if(chatOptional.isPresent()){
            return ResponseEntity.ok()
                    .body(chatOptional);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat "+id+" no existe");
    }

    private static ResponseEntity<Map<String,String>> validate(BindingResult result){
        Map<String,String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err ->{
            errors.put(err.getField(),"El campo "+err.getField() + " " +err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Chat chat, BindingResult result){
        if(result.hasErrors()){
            return validate(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(chat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody Chat chat, BindingResult result,@PathVariable Long id){
        if(result.hasErrors()){
            return validate(result);
        }
        Optional<Chat> chatOptional = service.byId(id);
        if(chatOptional.isPresent()){
            Chat chatDB = chatOptional.get();
            chatDB.setTitle(chat.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(chatDB));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat "+id+" no existe");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<Chat> chatOptional = service.byId(id);
        if(chatOptional.isPresent()){
            service.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Chat "+id+" eliminado exitosamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat "+id+" no existe");
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<?> addMessage(@RequestBody Message message,@PathVariable Long chatId){
        Optional<Message> o = service.addMessage(message,chatId);
        if(o.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatId){
        List<Message> messages  = service.getChatMessages(chatId);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/{chatId}/last-message")
    public ResponseEntity<?> getLastMessage(@PathVariable Long chatId){
        List<Message> messages = service.getChatMessages(chatId);
        if(!messages.isEmpty()){
            return ResponseEntity.ok().body(messages.get(messages.size()-1));
        }
        return ResponseEntity.notFound().build();
    }

}
