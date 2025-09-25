package com.bensiebert.codelib.chats.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.chats.data.Chat;
import com.bensiebert.codelib.chats.data.ChatMessage;
import com.bensiebert.codelib.chats.data.ChatMessageRepository;
import com.bensiebert.codelib.chats.data.ChatRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MessageController {

    @Autowired
    public ChatRepository chats;

    @Autowired
    public ChatMessageRepository messages;

    @Autowired
    public UserRepository users;

    @Operation(summary = "Create a chat message", tags = {"Messages"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = UnauthorizedResponse401.class)
            )),
    })
    @Authenticated
    @RequestMapping(path = "/chats/{id}/messages", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ChatMessage> createMessage(@Parameter(hidden = true) @CurrentUser User user, @PathVariable(name = "id", required = true) String id, @RequestBody String message) {
        if (id == null || id.isBlank() || message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Chat> ch = chats.findById(id);

        if (!ch.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Chat chat = ch.get();

        if (chat.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            return ResponseEntity.status(403).build();
        }

        ChatMessage msg = new ChatMessage();
        msg.setChat(chat);
        msg.setSenderId(user.getId());
        msg.setContent(message);
        msg.setTimestamp(System.currentTimeMillis());

        ChatMessage saved = messages.save(msg);

        return ResponseEntity.ok(sanitizeMessage(saved));
    }

    @Operation(summary = "Get messages in a chat", tags = {"Messages"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = UnauthorizedResponse401.class)
            ))
    })
    @Authenticated
    @RequestMapping(path = "/chats/{id}/messages", produces = "application/json", method = RequestMethod.GET)
    public List<ChatMessage> getMessages(@Parameter(hidden = true) @CurrentUser User user, @PathVariable(name = "id", required = true) String id,
                                         @RequestParam(name = "after", required = false) Long after,
                                         @RequestParam(name = "limit", required = false) Integer limit
    ) {
        Optional<Chat> ch = chats.findById(id);

        if (!ch.isPresent()) {
            return List.of();
        }

        Chat chat = ch.get();

        if (chat.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            return List.of();
        }

        List<ChatMessage> msgs;

        if (after != null && limit != null) {
            Pageable pageable = PageRequest.ofSize(limit);
            msgs = messages.findByChatAndTimestampGreaterThanOrderByTimestampDesc(chat, after, pageable.toLimit());
        } else if (after != null) {
            msgs = messages.findByChatAndTimestampGreaterThanOrderByTimestampDesc(chat, after);
        } else if (limit != null) {
            Pageable pageable = PageRequest.ofSize(limit);
            msgs = messages.findByChatOrderByTimestampDesc(chat, pageable.toLimit());
        } else {
            msgs = messages.findByChatOrderByTimestampDesc(chat);
        }

        return msgs.stream().map(this::sanitizeMessage).toList();
    }

    public ChatMessage sanitizeMessage(ChatMessage msg) {
        if (msg == null) {
            return null;
        }
        return msg.withChat(null);
    }
}
