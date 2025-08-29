package com.bensiebert.codelib.auth.primitive;

import com.bensiebert.codelib.auth.data.Token;
import com.bensiebert.codelib.auth.data.TokenRepository;
import com.bensiebert.codelib.auth.data.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Authentication {

    @Autowired
    private TokenRepository tkns;

    public static TokenRepository tokens;

    @PostConstruct
    public void init() {
        Authentication.tokens = tkns;
    }


    public static User getUserByHeader(String authHeader) {
        String tkn = BearerToken.extract(authHeader);

        if(tkn.isBlank()) return null;

        Optional<Token> tx = tokens.findById(tkn);

        if(tx.isEmpty()) return null;

        Token token = tx.get();

        if(token.getExpiryTime() < System.currentTimeMillis()) {
            tokens.delete(token);
            return null;
        }

        return token.getUser();
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRole().toLowerCase().equals("admin");
    }
}
