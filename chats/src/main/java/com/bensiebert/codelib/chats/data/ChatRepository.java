package com.bensiebert.codelib.chats.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRepository extends CrudRepository<Chat, String> {
    List<Chat> findByUserIdsContains(List<String> userIds);
}