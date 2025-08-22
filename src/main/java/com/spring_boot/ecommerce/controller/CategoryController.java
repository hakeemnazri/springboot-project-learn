// USE CUSTOMERS TO LEARN ABOUT BASIC OOP REST API, BEAN VALIDATION & GLOBAL EXCEPTION

package com.spring_boot.ecommerce.controller;

import com.spring_boot.ecommerce.config.AppConstants;
import com.spring_boot.ecommerce.payload.CategoryDTO;
import com.spring_boot.ecommerce.payload.CategoryResponse;
import com.spring_boot.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        ResponseEntity<CategoryResponse> categories = new ResponseEntity<>(
                categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder),
                HttpStatus.OK
        );

        return categories;
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<CategoryDTO>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(
            @PathVariable("categoryId") Long categoryId
    ){
        CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            @PathVariable("categoryId") Long categoryId
            ){

        CategoryDTO savedCategory = categoryService.updateCategory(categoryDTO, categoryId);

        return new ResponseEntity<CategoryDTO>(savedCategory, HttpStatus.OK);
    }
}
