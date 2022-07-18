//package com.example.autoposting.service;
//
//import com.example.autoposting.model.Post;
//import com.example.autoposting.model.QPost;
//import com.querydsl.core.types.dsl.DateTimePath;
//import com.querydsl.jpa.impl.JPAQuery;
//import lombok.RequiredArgsConstructor;
//
//import javax.persistence.EntityManager;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@RequiredArgsConstructor
//public class PostCustomService {
//    private final EntityManager entityManager;
//
//    public List<Post> filterByDate(LocalDateTime localDateTime) {
//
//        List<Post> fetch = null;
//        DateTimePath<LocalDateTime> createdDate = QPost.post.createdDate;
//        fetch = new JPAQuery<QPost>(entityManager)
//                .select(QPost.post)
//                .from(QPost.post)
//                .where(QPost.post.createdDate.eq(localDateTime.) && QPost.post.getCreatedDate().getMonth().equals(localDateTime.getMonth()))
//                .fetch();
//
//        return fetch;
//    }
//
//}
