package com.example.autoposting.repository;

import com.example.autoposting.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findAllByCreatedDate(String localDate);

}
