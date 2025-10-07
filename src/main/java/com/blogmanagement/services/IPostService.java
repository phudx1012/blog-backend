package com.blogmanagement.services;

import com.blogmanagement.dtos.request.PostRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;

public interface IPostService {

    ResponseEntity<?> getAllPosts(int page, int size, Integer categoryId, Integer tagId, String keyword, String status);

    ResponseEntity<?> getPostById(Integer id);

    ResponseEntity<?> createPost(PostRequest request);

    ResponseEntity<?> updatePost(Integer id, PostRequest request);

    ResponseEntity<?> deletePost(Integer id);
}
