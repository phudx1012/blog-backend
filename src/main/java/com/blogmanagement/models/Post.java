package com.blogmanagement.models;

import com.blogmanagement.utils.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.Set;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String slug; // ✅ thêm slug để dùng cho URL

    @Column(length = 500)
    private String excerpt; // tóm tắt ngắn

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl; // ảnh đại diện

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new java.util.HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new java.util.HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (slug == null || slug.isBlank()) {
            this.slug = toSlug(this.title);
        }
    }

    private String toSlug(String input) {
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
        return slug.toLowerCase();
    }
}
