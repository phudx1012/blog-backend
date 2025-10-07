package com.blogmanagement.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {
    private Integer id;
    private String name;
}
