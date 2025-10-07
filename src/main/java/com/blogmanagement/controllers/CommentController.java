package com.blogmanagement.controllers;

import com.blogmanagement.dtos.request.CommentRequest;
import com.blogmanagement.dtos.responses.CommentResponse;
import com.blogmanagement.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    // 🟢 Tạo comment cho 1 post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Integer postId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.createComment(postId, request));
    }

    // 🟢 Lấy danh sách comment của 1 post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(
            @PathVariable Integer postId
    ) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    // 🟢 Xóa comment
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
