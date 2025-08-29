package com.bensiebert.codelib.auth.data;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, String> {

    Optional<Token> findById(String id);
}
