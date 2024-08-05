package com.espe.usersdb.services;

import com.espe.usersdb.models.entity.User;
import com.espe.usersdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<User> list() {
        return (List<User>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> byId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        return (User) repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> byName(String name) {
        List<User> users = list();
        for(User user:users){
            if(user.getName().equals(name)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
