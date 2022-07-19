package com.example.autoposting.service;

import com.example.autoposting.model.Post;
import com.example.autoposting.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;


    public List<Post> findAll(){
        return postRepository.findAll();
    }

    public Optional<Post> findById(int id){
        return postRepository.findById(id);
    }

    public Post save(Post post){
        return postRepository.save(post);
    }

    public void deleteById(int id){
        postRepository.deleteById(id);
    }

    public List<Post> filterByDate(String localDate){
        return postRepository.findAllByCreatedDate(localDate);
    }

}
