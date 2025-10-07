package com.blogmanagement.services;

import com.blogmanagement.dtos.request.TagRequest;
import org.springframework.http.ResponseEntity;

public interface ITagService {
    ResponseEntity<?> getAllTags(int page, int size);

    ResponseEntity<?> getTagById(Integer id);

    ResponseEntity<?> createTag(TagRequest request);

    ResponseEntity<?> updateTag(Integer id, TagRequest request);

    ResponseEntity<?> deleteTag(Integer id);
}
