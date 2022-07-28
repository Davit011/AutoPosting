package com.example.autoposting.repository;

import com.example.autoposting.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {

    void deleteByPost_Id(int id);

}
