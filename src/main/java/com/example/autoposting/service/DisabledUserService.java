package com.example.autoposting.service;

import com.example.autoposting.model.DisabledUser;
import com.example.autoposting.repository.DisabledUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisabledUserService {

    private final DisabledUserRepository disabledUserRepository;

    public DisabledUser save(DisabledUser user){
        return disabledUserRepository.save(user);
    }

    public List<DisabledUser> findAll(){
        return disabledUserRepository.findAll();
    }

    public Optional<DisabledUser> findById(int id){
        return disabledUserRepository.findById(id);
    }

    public void deleteById(int id){
        disabledUserRepository.deleteById(id);
    }

    public Optional<DisabledUser> findByName(String name){ return disabledUserRepository.findByName(name); }

    public Optional<DisabledUser> findByprofileId(String profileId){ return disabledUserRepository.findByProfileId(profileId); }

}
