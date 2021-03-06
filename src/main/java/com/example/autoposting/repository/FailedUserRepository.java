package com.example.autoposting.repository;

import com.example.autoposting.model.FailedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedUserRepository extends JpaRepository<FailedUser, Integer> {

    void deleteAllByProfileId(String profileId);

}
