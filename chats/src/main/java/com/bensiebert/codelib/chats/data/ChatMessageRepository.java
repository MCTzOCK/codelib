package com.bensiebert.codelib.chats.data;

import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {
}