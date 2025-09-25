package com.bensiebert.codelib.chats.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.chats.data.Chat;
import com.bensiebert.codelib.chats.data.ChatRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ChatController {

    @Autowired
    public ChatRepository repo;

    @Autowired
    public UserRepository users;

    @Operation(summary = "Start a new chat", tags = {"Chats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created"),
            @ApiResponse(responseCode = "400", description = "Invalid userId supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = UnauthorizedResponse401.class)
            )),
    })
    @Authenticated
    @RequestMapping(path = "/chats", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<CreateChatResponse> createChat(@RequestParam(required = true, name = "userId") String userId, @Parameter(hidden = true) @CurrentUser User user) {
        if (userId == null || userId.isBlank() || userId.equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<User> usr = users.findById(userId);

        if (usr.isPresent()) {

            List<Chat> existing = this.getChatsOfUser(user);

            if (!existing.isEmpty()) {
                for(Chat c : existing) {
                    if(c.getUsers().contains(usr.get())) {
                        return new ResponseEntity<>(new CreateChatResponse(c.getId()), HttpStatus.OK);
                    }
                }
            }

            Chat chat = new Chat();
            chat.setUsers(List.of(this.getFromDetached(user), usr.get()));
            Chat saved = repo.save(chat);
            return new ResponseEntity<>(new CreateChatResponse(saved.getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all chats of the current user", tags = {"Chats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chats found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = UnauthorizedResponse401.class)
            )),
    })
    @Authenticated
    @RequestMapping(path = "/chats", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<List<ChatResponse>> getChats(@Parameter(hidden = true) @CurrentUser User user) {
        List<Chat> chats = getChatsOfUser(user);
        List<ChatResponse> response = chats.stream().map(c -> {
            ChatResponse cr = new ChatResponse();
            cr.setChatId(c.getId());
            cr.setUsernames(c.getUsers().stream().map(User::getUsername).toList());
            return cr;
        }).toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete a chat", tags = {"Chats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat deleted"),
            @ApiResponse(responseCode = "403", description = "No permission to delete this chat", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = UnauthorizedResponse401.class)
            )),
    })
    @Authenticated
    @RequestMapping(path = "/chats", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<List<Chat>> deleteChat(@Parameter(hidden = true) @CurrentUser User user, @RequestParam(required = true, name = "chatId") String chatId) {
        Optional<Chat> c = repo.findById(chatId);
        if (c.isPresent()) {
            Chat chat = c.get();
            if (chat.getUsers().contains(this.getFromDetached(user))) {
                repo.deleteById(chatId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Chat> getChatsOfUser(User user) {
        if(user == null) {
            return Collections.emptyList();
        }
        return repo.findAllByUser(user.getId());
    }

    public User getFromDetached(User user) {
        return users.findById(user.getId()).orElse(null);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CreateChatResponse {
        public String chatId;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ChatResponse {
        public String chatId;
        public List<String> usernames;
    }
}
