package com.example.autoposting.service;

import com.example.autoposting.model.FailedUser;
import com.example.autoposting.repository.FailedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FailedUserService {

    private final FailedUserRepository failedUserRepository;

    public FailedUser save(FailedUser failedUser) {
        return failedUserRepository.save(failedUser);
    }

    @Transactional
    public void deleteAllRepeats(String id) {
        failedUserRepository.deleteAllByProfileId(id);
    }

    public List<FailedUser> findAll() {
        return failedUserRepository.findAll();
    }
}
