package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.model.Category;
import com.spring_boot.ecommerce.payload.CategoryDTO;
import com.spring_boot.ecommerce.payload.CategoryResponse;


public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO category, Long categoryId);
}
