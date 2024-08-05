package com.espe.usersdb.repositories;

import com.espe.usersdb.models.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {
}
