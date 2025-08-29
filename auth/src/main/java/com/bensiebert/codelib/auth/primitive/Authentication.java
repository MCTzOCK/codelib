package com.bensiebert.codelib.auth.primitive;

import com.bensiebert.codelib.auth.sql.data.Token;
import com.bensiebert.codelib.auth.sql.data.TokenRepository;
import com.bensiebert.codelib.auth.sql.data.User;
import com.bensiebert.codelib.auth.sql.data.UserRepository;
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

        if(tx.isPresent()) return null;

        Token token = tx.get();

        if(token.getExpiryTime() < System.currentTimeMillis()) {
            tokens.delete(token);
            return null;
        }

        return token.getUser();
    }
}
