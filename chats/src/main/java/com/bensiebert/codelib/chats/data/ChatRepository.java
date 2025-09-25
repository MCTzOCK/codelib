package com.bensiebert.codelib.chats.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, String> {

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findAllByUser(@Param("userId") String userId);

    List<Chat> findAllByUsersContainsIgnoreCase(List<String> users);
}