package com.blogmanagement.dtos.responses;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class PostResponse {

    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private String status;
    private String slug;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String author;

    private String categoryName;

    private CategoryResponse category;

    private Set<TagResponse> tags;

    private List<CommentResponse> comments;
}
