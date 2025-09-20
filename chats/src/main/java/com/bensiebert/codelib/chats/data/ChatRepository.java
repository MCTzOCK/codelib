package com.bensiebert.codelib.chats.data;

import com.bensiebert.codelib.auth.data.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface ChatRepository extends CrudRepository<Chat, String> {
    List<Chat> findChatByUsersContaining(List<User> users);


}