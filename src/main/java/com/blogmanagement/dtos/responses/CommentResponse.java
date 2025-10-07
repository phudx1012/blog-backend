package com.blogmanagement.dtos.responses;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Integer id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
}
