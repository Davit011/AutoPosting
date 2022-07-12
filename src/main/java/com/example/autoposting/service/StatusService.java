package com.example.autoposting.service;

import com.example.autoposting.model.Status;
import com.example.autoposting.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;

    public Status save(Status status){
        return statusRepository.save(status);
    }

}
