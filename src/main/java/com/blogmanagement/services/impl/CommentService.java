package com.blogmanagement.services.impl;

import com.blogmanagement.dtos.request.CommentRequest;
import com.blogmanagement.dtos.responses.CommentResponse;
import com.blogmanagement.models.Comment;
import com.blogmanagement.models.Post;
import com.blogmanagement.repositories.CommentRepository;
import com.blogmanagement.repositories.PostRepository;
import com.blogmanagement.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public CommentResponse createComment(Integer postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .authorName(request.getAuthorName())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .post(post)
                .build();

        comment = commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Integer postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .authorName(c.getAuthorName())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public void deleteComment(Integer commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
}
