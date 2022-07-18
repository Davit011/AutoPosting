package com.example.autoposting.repository;

import com.example.autoposting.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {

}
