package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.exceptions.APIException;
import com.spring_boot.ecommerce.exceptions.ResourceNotFoundException;
import com.spring_boot.ecommerce.model.Category;
import com.spring_boot.ecommerce.payload.CategoryDTO;
import com.spring_boot.ecommerce.payload.CategoryResponse;
import com.spring_boot.ecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private ModelMapper modelMapper;


    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        Category category = categoryOptional.orElseThrow(() -> new ResourceNotFoundException(categoryId, "categoryId", "Category"));

        categoryRepository.delete(category);

        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

        return categoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);

        Category savedCategory = savedCategoryOptional.orElseThrow(() -> new ResourceNotFoundException(categoryId, "categoryId", "Category"));

        savedCategory.setCategoryName(category.getCategoryName());

        Category savedCategoryDB = categoryRepository.save(savedCategory);

        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategoryDB, CategoryDTO.class);

        return savedCategoryDTO;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categoryPageContent = categoryPage.getContent();

        if(categoryPageContent.isEmpty()){
            throw new APIException("No categories found");
        }

        List<CategoryDTO> categoryDTOS = categoryPageContent.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory (CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if(savedCategory != null){
            throw new APIException("Category with name " + categoryDTO.getCategoryName() + " already exists!!");
        }

        Category savedCategoryRepo = categoryRepository.save(category);

        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategoryRepo, CategoryDTO.class);

        return savedCategoryDTO;
    }
}
