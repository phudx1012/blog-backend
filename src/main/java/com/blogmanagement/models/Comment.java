package com.blogmanagement.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String authorName; // tên người bình luận

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content; // nội dung bình luận

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
