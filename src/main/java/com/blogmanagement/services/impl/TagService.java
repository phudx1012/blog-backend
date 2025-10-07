package com.blogmanagement.services.impl;

import com.blogmanagement.dtos.request.TagRequest;
import com.blogmanagement.dtos.responses.TagResponse;
import com.blogmanagement.dtos.responses.ResponseObject;
import com.blogmanagement.models.Tag;
import com.blogmanagement.repositories.TagRepository;
import com.blogmanagement.services.ITagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService {
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;

    @Override
    @Cacheable(value = "tags", key = "{#page, #size}")
    public ResponseEntity<?> getAllTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Tag> tags = tagRepository.findAll(pageable);

        List<TagResponse> responses = tags.getContent()
                .stream()
                .map(this::mapTag)
                .toList();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", responses);
        data.put("page", Map.of(
                "size", tags.getSize(),
                "number", tags.getNumber(),
                "totalElements", tags.getTotalElements(),
                "totalPages", tags.getTotalPages()
        ));

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get tags successfully")
                        .data(data)
                        .build()
        );
    }

    private TagResponse mapTag(Tag tag) {
        return modelMapper.map(tag, TagResponse.class);
    }

    @Override
    @Cacheable(value = "tags", key = "{#id}")
    public ResponseEntity<?> getTagById(Integer id) {
        return tagRepository.findById(id)
                .map(tag -> ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .message("Get tags successfully")
                                .data(mapTag(tag))
                                .build()
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Tag not found")
                                .build()
                ));
    }

    @Override
    @CacheEvict(value = "tags", allEntries = true)
    public ResponseEntity<?> createTag(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        Tag saved = tagRepository.save(tag);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Tag created successfully")
                        .data(mapTag(saved))
                        .build()
        );
    }

    @Override
    @CacheEvict(value = "tags", allEntries = true)
    public ResponseEntity<?> updateTag(Integer id, TagRequest request) {
        Optional<Tag> optionalTag = tagRepository.findById(id);
        if (optionalTag.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Tag not found")
                            .build()
            );
        }

        Tag tag = optionalTag.get();
        tag.setName(request.getName());

        Tag updated = tagRepository.save(tag);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Tag updated successfully")
                        .data(mapTag(updated))
                        .build()
        );
    }


    @Override
    @CacheEvict(value = "tags", allEntries = true)
    public ResponseEntity<?> deleteTag(Integer id) {
        if (!tagRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Tag not found")
                            .build()
            );
        }

        tagRepository.deleteById(id);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Tag deleted successfully")
                        .build()
        );
    }

}
