package com.example.autoposting.repository;

import com.example.autoposting.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findTokenByToken(String token);

    Optional<Token> findTokenByProfileId(String id);

}
