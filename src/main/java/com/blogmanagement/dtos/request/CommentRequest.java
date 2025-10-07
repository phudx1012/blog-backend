package com.blogmanagement.dtos.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    @NotBlank
    @Size(min = 1, max = 20)
    private String authorName;

    @NotBlank
    private String content;

    private LocalDateTime createdAt;
}
