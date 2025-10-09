package com.blogmanagement.controllers;

import com.blogmanagement.dtos.request.PostRequest;
import com.blogmanagement.dtos.responses.ResponseObject;
import com.blogmanagement.services.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PostController {
    private final IPostService postService;

    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return postService.getAllPosts(page, size, categoryId, tagId, keyword, status);
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        return postService.getPostById(id);
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody PostRequest request,
            BindingResult result
            ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        return postService.createPost(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer id,
            @RequestBody PostRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        return postService.updatePost(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        return postService.deletePost(id);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        return postService.getPostBySlug(slug);
    }

}
