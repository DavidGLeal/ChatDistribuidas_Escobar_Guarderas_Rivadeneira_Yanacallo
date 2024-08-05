package com.espe.chatsdb.repositories;

import com.espe.chatsdb.models.entity.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message,Long> {
}
