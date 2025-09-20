package com.bensiebert.codelib.chats.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.chats.data.Chat;
import com.bensiebert.codelib.chats.data.ChatRepository;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ResponseEntity<Chat> createChat(@RequestParam(required = true, name = "userId") String userId, @Parameter(hidden = true) @CurrentUser User user) {
        if (userId == null || userId.isBlank() || userId.equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<User> usr = users.findById(userId);

        if (usr.isPresent()) {

            List<Chat> existing = repo.findChatByUsersContaining(List.of(user, usr.get()));

            if (!existing.isEmpty()) {
                return new ResponseEntity<>(existing.getFirst(), HttpStatus.OK);
            }

            Chat chat = new Chat();
            chat.setUsers(List.of(user, usr.get()));
            Chat saved = repo.save(chat);
            return new ResponseEntity<>(saved, HttpStatus.OK);
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
    public ResponseEntity<List<Chat>> getChats(@Parameter(hidden = true) @CurrentUser User user) {
        return new ResponseEntity<>(repo.findChatByUsersContaining(List.of(user)), HttpStatus.OK);
    }

    @Operation(summary = "Delete a chat", tags = {"Chats"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = UnauthorizedResponse401.class)
            )),
    })
    @Authenticated
    @RequestMapping(path = "/chats", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<List<Chat>> deleteChat(@Parameter(hidden = true) @CurrentUser User user, @RequestParam(required = true, name = "chatId") String chatId) {
        Optional<Chat> c = repo.findById(chatId);
        if (c.isPresent() && c.get().getUsers().contains(user)) {
            repo.delete(c.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
