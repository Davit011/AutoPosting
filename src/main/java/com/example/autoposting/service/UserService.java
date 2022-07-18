package com.example.autoposting.service;

import com.example.autoposting.model.User;
import com.example.autoposting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(int id){
        return userRepository.findById(id);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public void deleteById(int id){
        userRepository.deleteById(id);
    }

    public boolean findByProfileId(String id){
        Optional<User> byProfileId = userRepository.findByProfileId(id);
        return byProfileId.isPresent();
    }

    public Optional<User> findByName(String name){
        return userRepository.findUserByName(name);
    }

}
