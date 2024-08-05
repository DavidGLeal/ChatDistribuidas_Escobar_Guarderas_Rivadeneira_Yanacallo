package com.espe.usersdb.controllers;

import com.espe.usersdb.models.entity.User;
import com.espe.usersdb.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<List<User>> list() {
        List<User> users = service.list();
        if(users.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(null);
        }
        return ResponseEntity.ok()
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){
        Optional<User> userOptional = service.byId(id);
        if(userOptional.isPresent())
            return ResponseEntity.ok()
                    .body(userOptional);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ID: "+id+" no encontrado");
    }

    private static ResponseEntity<Map<String,String>> validate(BindingResult result){
        Map<String,String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err ->{
            errors.put(err.getField(),"El campo "+err.getField() + " " +err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result){
        if(result.hasErrors()){
            return validate(result);
        }
        user.setConnected(true);
        service.save(user);
        System.out.println(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User seekingUser){
        Optional<User> foundUser = service.byName(seekingUser.getName());
        System.out.println(foundUser.get());
        if(foundUser.isPresent() && seekingUser.getPassword().equals(foundUser.get().getPassword())){
            foundUser.get().setConnected(true);
            service.save(foundUser.get());
            return ResponseEntity.ok(foundUser.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody User user,BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validate(result);
        }

        Optional<User> userOptional = service.byId(id);

        if(userOptional.isPresent()){
            User userDB = userOptional.get();
            userDB.setName(user.getName());
            userDB.setPassword(user.getPassword());
            User updatedUser = service.save(userDB);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ID: "+id+" no encontrado");
    }

    @PutMapping("/{id}/connect")
    public ResponseEntity<?> connect(@PathVariable Long id){

        Optional<User> userOptional = service.byId(id);

        if(userOptional.isPresent()){
            User userDB = userOptional.get();
            userDB.setConnected(true);
            User updatedUser = service.save(userDB);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ID: "+id+" no encontrado");
    }

    @PutMapping("/{id}/disconnect")
    public ResponseEntity<?> disconnect(@PathVariable Long id){
        Optional<User> userOptional = service.byId(id);
        if(userOptional.isPresent()){
            User userDB = userOptional.get();
            userDB.setConnected(false);
            User updatedUser = service.save(userDB);
            System.out.println(updatedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ID: "+id+" no encontrado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<User> optionalUser = service.byId(id);
        if(optionalUser.isPresent()){
            service.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Usuario con ID: "+id+" eliminado exitosamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con ID: "+id+" no encontrado");
    }

}
