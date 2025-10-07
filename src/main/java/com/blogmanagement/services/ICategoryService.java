package com.blogmanagement.services;

import com.blogmanagement.dtos.request.CategoryRequest;
import org.springframework.http.ResponseEntity;

public interface ICategoryService {
    ResponseEntity<?> getAllCategories(int page, int size);

    ResponseEntity<?> getCategoryById(Integer id);

    ResponseEntity<?> createCategory(CategoryRequest request);

    ResponseEntity<?> updateCategory(Integer id, CategoryRequest request);

    ResponseEntity<?> deleteCategory(Integer id);
}
