package com.example.autoposting.service;

import com.example.autoposting.model.Token;
import com.example.autoposting.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public Token save(Token token){
        return tokenRepository.save(token);
    }

    public List<Token> findAll(){ return tokenRepository.findAll(); }

    public Optional<Token> findTokenByToken(String token){
        return tokenRepository.findTokenByToken(token);
    }

    public Optional<Token> findByProfileId(String id){
        return tokenRepository.findTokenByProfileId(id);
    }

}
