package com.espe.usersdb.services;

import com.espe.usersdb.models.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> list();
    Optional<User> byId(Long id);
    Optional<User> byName(String name);
    User save(User user);
    void delete(Long id);
}
