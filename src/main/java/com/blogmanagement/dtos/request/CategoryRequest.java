package com.blogmanagement.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank(message = "Category name must not be empty")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Description must not be empty")
    private String description;
}