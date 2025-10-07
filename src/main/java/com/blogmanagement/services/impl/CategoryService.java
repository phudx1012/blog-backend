package com.blogmanagement.services.impl;

import com.blogmanagement.dtos.request.CategoryRequest;
import com.blogmanagement.dtos.responses.CategoryResponse;
import com.blogmanagement.dtos.responses.ResponseObject;
import com.blogmanagement.models.Category;
import com.blogmanagement.repositories.CategoryRepository;
import com.blogmanagement.repositories.PostRepository;
import com.blogmanagement.repositories.TagRepository;
import com.blogmanagement.services.ICategoryService;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Cacheable(value = "categories", key = "{#page, #size}")
    public ResponseEntity<?> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);

        List<CategoryResponse> responses = categories.getContent()
                .stream()
                .map(this::mapCategory)
                .toList();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", responses);
        data.put("page", Map.of(
                "size", categories.getSize(),
                "number", categories.getNumber(),
                "totalElements", categories.getTotalElements(),
                "totalPages", categories.getTotalPages()
        ));

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get categories successfully")
                        .data(data)
                        .build()
        );
    }

    private CategoryResponse mapCategory(Category category) {
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    @Cacheable(value = "categories", key = "{#id}")
    public ResponseEntity<?> getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .message("Get categories successfully")
                                .data(mapCategory(category))
                                .build()
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Category not found")
                                .build()
                ));
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseEntity<?> createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        Category saved = categoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Category created successfully")
                        .data(mapCategory(saved))
                        .build()
        );
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseEntity<?> updateCategory(Integer id, CategoryRequest request) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Category not found")
                            .build()
            );
        }

        Category category = optionalCategory.get();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Category updated successfully")
                        .data(mapCategory(updated))
                        .build()
        );
    }


    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseEntity<?> deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Category not found")
                            .build()
            );
        }

        categoryRepository.deleteById(id);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Category deleted successfully")
                        .build()
        );
    }

}
