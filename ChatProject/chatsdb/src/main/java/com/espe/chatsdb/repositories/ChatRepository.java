package com.espe.chatsdb.repositories;

import com.espe.chatsdb.models.entity.Chat;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<Chat,Long> {
}
