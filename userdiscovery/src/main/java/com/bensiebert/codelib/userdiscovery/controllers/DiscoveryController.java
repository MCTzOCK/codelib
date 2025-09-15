package com.bensiebert.codelib.userdiscovery.controllers;

import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.userdiscovery.UserStub;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscoveryController {

    @Autowired
    private UserRepository repo;

    @Operation(summary = "Discover Users", tags = {"Discovery"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of users matching the search criteria",
                content = @Content(mediaType = "application/json", array = @ArraySchema(
                        schema = @Schema(implementation = UserStub.class)
                ))
            ),
    })
    @RequestMapping(value = "/userdiscovery", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public UserStub[] discoverUsers(@RequestParam(required = false, name = "name") String name, @RequestParam(required = false, name = "page") Integer page) {

        if (name != null && !name.isBlank()) {
            Specification<User> spec = (root, query, cb) -> {
                String pattern = "%" + name.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                );
            };
            return repo.findAll(spec).stream().map(UserStub::of).toArray(UserStub[]::new);
        }

        Pageable pageable = Pageable.ofSize(20);
        if (page != null && page > 0) {
            pageable = Pageable.ofSize(20).withPage(page);
        }

        return repo.findAll(pageable).stream().map(UserStub::of).toArray(UserStub[]::new);
    }
}
