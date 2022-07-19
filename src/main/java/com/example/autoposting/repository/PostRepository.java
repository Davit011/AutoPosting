package com.example.autoposting.repository;

import com.example.autoposting.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {

    List<Post> findAllByCreatedDate(String localDate);

}
