package com.example.autoposting.repository;

import com.example.autoposting.model.DisabledUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisabledUserRepository extends JpaRepository<DisabledUser, Integer> {

    Optional<DisabledUser> findByName(String name);

    Optional<DisabledUser> findByProfileId(String profileId);

}
