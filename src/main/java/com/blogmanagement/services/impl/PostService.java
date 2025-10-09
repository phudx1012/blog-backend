package com.blogmanagement.services.impl;

import com.blogmanagement.dtos.request.PostRequest;
import com.blogmanagement.dtos.responses.*;
import com.blogmanagement.models.Category;
import com.blogmanagement.models.Comment;
import com.blogmanagement.models.Post;
import com.blogmanagement.models.Tag;
import com.blogmanagement.repositories.CategoryRepository;
import com.blogmanagement.repositories.PostRepository;
import com.blogmanagement.repositories.TagRepository;
import com.blogmanagement.services.IPostService;
import com.blogmanagement.utils.PostSpecification;
import com.blogmanagement.utils.PostStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Cacheable(value = "posts", key = "{#page, #size, #categoryId, #tagId, #keyword, #status}")
    @Override
    public ResponseEntity<?> getAllPosts(int page, int size, Integer categoryId, Integer tagId, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Post> spec = Specification.allOf(
                PostSpecification.hasCategory(categoryId),
                PostSpecification.hasTag(tagId),
                PostSpecification.hasKeyword(keyword),
                PostSpecification.hasStatus(status)
        );

        Page<Post> posts = postRepository.findAll(spec, pageable);

        List<PostResponse> responses = posts.getContent()
                .stream()
                .map(this::mapPost)
                .toList();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", responses);
        data.put("page", Map.of(
                "size", posts.getSize(),
                "number", posts.getNumber(),
                "totalElements", posts.getTotalElements(),
                "totalPages", posts.getTotalPages()
        ));

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get posts successfully")
                        .data(data)
                        .build()
        );
    }

    @Override
    @Cacheable(value = "posts", key = "{ #id}")
    public ResponseEntity<?> getPostById(Integer id) {
        return postRepository.findById(id)
                .map(post -> ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .message("Get post successfully")
                                .data(mapPost(post))
                                .build()
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Post not found")
                                .build()
                ));
    }

    @Override
    @CacheEvict(value = "posts", allEntries = true)
    public ResponseEntity<?> createPost(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setStatus(request.getStatus());
        post.setCreatedAt(request.getCreatedAt());
        post.setAuthor(request.getAuthor());
        post.setThumbnailUrl(request.getThumbnailUrl());

        // Category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        }

        // Tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            post.setTags(tags);
        }

        Post saved = postRepository.save(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Post created successfully")
                        .data(mapPost(saved))
                        .build()
        );
    }

    @Override
    @CacheEvict(value = "posts", allEntries = true)
    public ResponseEntity<?> updatePost(Integer id, PostRequest request) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Post not found")
                            .build()
            );
        }

        Post post = optionalPost.get();
        post.setTitle(request.getTitle());
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setStatus(request.getStatus());
        post.setThumbnailUrl(request.getThumbnailUrl());
        post.setAuthor(request.getAuthor());
        post.setCreatedAt(request.getCreatedAt());

        // Update category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            post.setCategory(category);
        }

        // Update tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            post.setTags(tags);
        } else {
            post.getTags().clear();
        }

        post.setUpdatedAt(LocalDateTime.now());

        Post updated = postRepository.save(post);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Post updated successfully")
                        .data(mapPost(updated))
                        .build()
        );
    }


    @Override
    @CacheEvict(value = "posts", allEntries = true)
    public ResponseEntity<?> deletePost(Integer id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Post not found")
                            .build()
            );
        }
        Post post = optionalPost.get();
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Post deleted successfully")
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        return ResponseEntity.ok(
                ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Post found successfully")
                .data(mapPost(post))
                .build()
        );
    }

    private PostResponse mapPost(Post post) {
        PostResponse response = modelMapper.map(post, PostResponse.class);

        // ✅ Category
        if (post.getCategory() != null) {
            response.setCategoryName(post.getCategory().getName());
            response.setCategory(
                    modelMapper.map(post.getCategory(), CategoryResponse.class)
            );
        }

        // ✅ Tags (set TagResponse đúng kiểu)
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            response.setTags(
                    post.getTags()
                            .stream()
                            .map(tag -> TagResponse.builder()
                                    .id(tag.getId())
                                    .name(tag.getName())
                                    .build())
                            .collect(Collectors.toSet())
            );
        } else {
            response.setTags(Collections.emptySet());
        }

        // ✅ Comments (map nhanh sang CommentResponse)
        if (post.getComments() != null && !post.getComments().isEmpty()) {
            response.setComments(
                    post.getComments()
                            .stream()
                            .sorted(Comparator.comparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                            .map(c -> CommentResponse.builder()
                                    .id(c.getId())
                                    .authorName(c.getAuthorName())
                                    .content(c.getContent())
                                    .createdAt(c.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList())
            );
        } else {
            response.setComments(Collections.emptyList());
        }

        return response;
    }

}
