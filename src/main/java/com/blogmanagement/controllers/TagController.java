package com.blogmanagement.controllers;

import com.blogmanagement.dtos.request.TagRequest;
import com.blogmanagement.dtos.responses.ResponseObject;
import com.blogmanagement.services.ITagService;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TagController {
    private final ITagService tagService;

    @GetMapping
    public ResponseEntity<?> getTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return tagService.getAllTags(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTagById(@PathVariable Integer id) {
        return tagService.getTagById(id);
    }

    @PostMapping
    public ResponseEntity<?> createTag(
            @RequestBody TagRequest request,
            BindingResult result
            ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        return tagService.createTag(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(
            @PathVariable Integer id,
            @RequestBody TagRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.valueOf(errorMessages))
                            .build()
            );
        }
        return tagService.updateTag(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Integer id) {
        return tagService.deleteTag(id);
    }
}
