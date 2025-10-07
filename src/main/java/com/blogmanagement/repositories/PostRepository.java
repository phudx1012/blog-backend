package com.blogmanagement.repositories;

import com.blogmanagement.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> , JpaSpecificationExecutor<Post> {
    Optional<Post> findById(Integer id);


}
