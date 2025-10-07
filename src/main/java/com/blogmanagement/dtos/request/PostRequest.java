package com.blogmanagement.dtos.request;

import com.blogmanagement.utils.PostStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

        @NotBlank(message = "Title must not be empty")
        @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
        private String title;

        @NotBlank(message = "Excerpt must not be empty")
        @Size(min = 10, max = 300, message = "Excerpt must be between 10 and 300 characters")
        private String excerpt;

        @NotBlank(message = "Content must not be empty")
        @Size(min = 20, message = "Content must be at least 20 characters long")
        private String content;

        @NotNull(message = "Status is required")
        private PostStatus status;

        @Pattern(
                regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|png|gif)?$",
                message = "Thumbnail URL must be a valid image URL (jpg, jpeg, png, gif)"
        )
        private String thumbnailUrl;

        private String author;


        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number")
        private Integer categoryId;

        private Set<@Positive(message = "Tag ID must be a positive number") Integer> tagIds = new HashSet<>();

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}
