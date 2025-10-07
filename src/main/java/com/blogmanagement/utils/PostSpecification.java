package com.blogmanagement.utils;

import com.blogmanagement.models.Post;
import org.springframework.data.jpa.domain.Specification;
public class PostSpecification {

    public static Specification<Post> hasCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Post> hasTag(Integer tagId) {
        return (root, query, cb) -> {
            if (tagId == null) return null;
            return cb.isTrue(root.join("tags").get("id").in(tagId));
        };
    }

    public static Specification<Post> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("content")), pattern),
                    cb.like(cb.lower(root.get("excerpt")), pattern)
            );
        };
    }

    public static Specification<Post> hasStatus(String status) {
        return (root, query, cb) -> status == null || status.trim().isEmpty()
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }
}
