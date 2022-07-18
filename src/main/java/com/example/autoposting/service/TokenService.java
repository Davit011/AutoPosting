package com.example.autoposting.service;

import com.example.autoposting.model.Token;
import com.example.autoposting.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public Token save(Token token){
        return tokenRepository.save(token);
    }


}
