package com.blogmanagement.services;

import com.blogmanagement.dtos.request.CommentRequest;
import com.blogmanagement.dtos.responses.CommentResponse;

import java.util.List;

public interface ICommentService {
    CommentResponse createComment(Integer postId, CommentRequest request);

    List<CommentResponse> getCommentsByPostId(Integer postId);

    void deleteComment(Integer commentId);
}
