package com.bensiebert.codelib.chats.data;

import org.springframework.data.domain.Limit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {


    List<ChatMessage> findByChatAndTimestampGreaterThanOrderByTimestampAsc(Chat chat, Long timestampIsGreaterThan, Limit limit);

    List<ChatMessage> findByChatAndTimestampGreaterThanOrderByTimestampAsc(Chat chat, Long timestampIsGreaterThan);

    List<ChatMessage> findByChatOrderByTimestampAsc(Chat chat, Limit limit);

    List<ChatMessage> findByChatOrderByTimestampAsc(Chat chat);
}